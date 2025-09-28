package work.chncyl.system.dictionary.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.chncyl.base.global.Constants;
import work.chncyl.base.global.redis.RedisUtils;
import work.chncyl.system.dictionary.dto.input.*;
import work.chncyl.system.dictionary.dto.output.DictionaryTreeNode;
import work.chncyl.system.dictionary.dto.output.DictionaryTypeInfo;
import work.chncyl.system.dictionary.entity.Dictionary;
import work.chncyl.system.dictionary.mapper.DictionaryMapper;
import work.chncyl.system.dictionary.service.DictionaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

    @Override
    @Transactional
    public Integer addDictionary(DictionaryAddInput input) {
        if (StrUtil.hasBlank(input.getType(), input.getCode())) {
            throw new IllegalArgumentException("类型和码值不能为空");
        }
        // 判断是否重复
        long count = count(new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getCode, input.getCode())
                .eq(Dictionary::getType, input.getType())
        );
        if (count > 0) {
            throw new IllegalArgumentException("数据字典已存在");
        }
        // 获取类型信息
        Dictionary typeInfo = getOne(new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getType, input.getType())
                .isNotNull(Dictionary::getTypeName)
        );
        if (typeInfo != null) {
            if (StrUtil.isBlank(input.getTypeName())) {
                input.setTypeName(typeInfo.getTypeName());
            } else if (!input.getTypeName().equals(typeInfo.getTypeName())) {
                // 类型形同，但类型名称不同，可能是不同类型
                throw new IllegalArgumentException("类型名称不同,请确认是否为相同类型");
            }
        } else if (StrUtil.isNotBlank(input.getTypeName())) {
            // 当前类型数据库都没有名称，但传入的有，同步至数据库
            update(new LambdaUpdateWrapper<Dictionary>()
                    .set(Dictionary::getTypeName, input.getTypeName())
                    .eq(Dictionary::getType, input.getType()));
        }
        Dictionary dictionary = BeanUtil.copyProperties(input, Dictionary.class);
        // 判断上级 设置层级
        if (input.getUpId() != null) {
            Dictionary one = getOne(new LambdaQueryWrapper<Dictionary>()
                    .eq(Dictionary::getId, input.getUpId())
                    .eq(Dictionary::getType, input.getType())
            );
            if (one == null) {
                throw new IllegalArgumentException("上级节点不存在");
            }
            dictionary.setLevel((byte) (one.getLevel() + 1));
        } else {
            // 没有上级
            dictionary.setLevel((byte) 0);
        }
        save(dictionary);
        updateCache(null);
        return dictionary.getId();
    }

    @Override
    public Boolean delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("id不能为空");
        }
        boolean b = removeById(id);
        if (b) {
            updateCache(null);
        }
        return b;
    }

    @Override
    public Boolean updateDictionary(DictionaryUpdateInput input) {
        if (input.getId() == null) {
            throw new IllegalArgumentException("id不能为空");
        }
        Dictionary byId = getById(input.getId());
        if (byId == null) {
            throw new IllegalArgumentException("数据字典不存在");
        }

        if (!input.getCode().equals(byId.getCode())) {
            // 码值更改，查看是否重复
            long count = count(new LambdaQueryWrapper<Dictionary>()
                    .eq(Dictionary::getCode, input.getCode())
                    .eq(Dictionary::getType, byId.getType())
            );
            if (count > 0) {
                throw new IllegalArgumentException("数据字典已存在");
            }
        }
        if (!Objects.equals(byId.getUpId(), input.getUpId())) {
            // 上级更改
            if (input.getUpId() != null) {
                //查看是否存在
                Dictionary one = getOne(new LambdaQueryWrapper<Dictionary>()
                        .eq(Dictionary::getId, input.getId())
                        .eq(Dictionary::getType, byId.getType())
                );
                if (one == null) {
                    throw new IllegalArgumentException("上级节点不存在");
                }
                byId.setLevel((byte) (one.getLevel() + 1));
            } else {
                byId.setLevel((byte) 0);
            }
        }
        BeanUtil.copyProperties(input, byId);
        boolean b = updateById(byId);
        if (b) {
            updateCache(null);
        }
        return b;
    }

    @Override
    public List<Dictionary> getDictionaryDetail(DictionarySearchInfo info) {
        return baseMapper.getInfos(info);
    }

    @Override
    public Map<String, List<DictionaryTreeNode>> dictionaryTree(DictionaryTreeInput input) {
        // 查缓存
        Map<String, List<DictionaryTreeNode>> result = RedisUtils.get(Constants.DICTIONARY_KEY);
        if (result != null) {
            return result;
        }
        // 没有缓存，查数据库
        DictionarySearchInfo info = new DictionarySearchInfo();
        if (StrUtil.isNotBlank(input.getType())) {
            info.setType(input.getType());
        }
        List<Dictionary> list = getDictionaryDetail(info);

        result = generateTree(list);
        updateCache(result);
        return result;
    }

    @Override
    public List<DictionaryTypeInfo> getDictionaryType(DictionaryTypeSearch input) {
        return baseMapper.getDictionaryType(input);
    }

    private Map<String, List<DictionaryTreeNode>> generateTree(List<Dictionary> list) {
        Map<String, List<DictionaryTreeNode>> result = new HashMap<>();
        // 按不同的Type分组
        Map<String, List<Dictionary>> groupedByType = list.stream()
                .collect(Collectors.groupingBy(Dictionary::getType));

        groupedByType.forEach((type, dictionaries) -> {
            Map<Integer, DictionaryTreeNode> dictionaryMap = new HashMap<>();
            List<DictionaryTreeNode> roots = new ArrayList<>();

            // 构建DictionaryTreeNode映射
            for (Dictionary dictionary : dictionaries) {
                DictionaryTreeNode node = new DictionaryTreeNode();
                node.setId(dictionary.getId());
                node.setCode(dictionary.getCode());
                node.setName(dictionary.getName());
                node.setUpId(dictionary.getUpId());
                node.setChildren(new ArrayList<>());
                dictionaryMap.put(node.getId(), node);
            }

            // 构建树结构
            for (DictionaryTreeNode node : dictionaryMap.values()) {
                if (node.getUpId() == null) {
                    roots.add(node);
                } else {
                    DictionaryTreeNode parent = dictionaryMap.get(node.getUpId());
                    if (parent != null) {
                        if (parent.getChildren() == null) {
                            parent.setChildren(new ArrayList<>());
                        }
                        parent.getChildren().add(node);
                    }
                }
            }
            result.put(type, roots);
        });

        return result;
    }

    private void updateCache(Map<String, List<DictionaryTreeNode>> result) {
        RedisUtils.delete(Constants.DICTIONARY_KEY);
        // 更新缓存
        if (result == null) {
            result = dictionaryTree(new DictionaryTreeInput());
        }
        RedisUtils.set(Constants.DICTIONARY_KEY, result);
    }
}
