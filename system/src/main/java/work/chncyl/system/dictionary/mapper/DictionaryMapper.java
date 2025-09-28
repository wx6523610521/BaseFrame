package work.chncyl.system.dictionary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import work.chncyl.system.dictionary.dto.input.DictionarySearchInfo;
import work.chncyl.system.dictionary.dto.input.DictionaryTypeSearch;
import work.chncyl.system.dictionary.dto.output.DictionaryTypeInfo;
import work.chncyl.system.dictionary.entity.Dictionary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DictionaryMapper extends BaseMapper<Dictionary> {
    List<Dictionary> getInfos(@Param("info") DictionarySearchInfo info);

    List<DictionaryTypeInfo> getDictionaryType(@Param("info") DictionaryTypeSearch input);
}