package work.chncyl.system.dictionary.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.chncyl.system.dictionary.dto.input.*;
import work.chncyl.system.dictionary.dto.output.DictionaryTreeNode;
import work.chncyl.system.dictionary.dto.output.DictionaryTypeInfo;
import work.chncyl.system.dictionary.entity.Dictionary;

import java.util.List;
import java.util.Map;

public interface DictionaryService extends IService<Dictionary> {

    Integer addDictionary(DictionaryAddInput input);

    Boolean delete(Integer id);

    Boolean updateDictionary(DictionaryUpdateInput input);

    List<Dictionary> getDictionaryDetail(DictionarySearchInfo info);

    Map<String, List<DictionaryTreeNode>> dictionaryTree(DictionaryTreeInput input);

    List<DictionaryTypeInfo> getDictionaryType(DictionaryTypeSearch input);
}
