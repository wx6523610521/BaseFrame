package work.chncyl.system.dictionary.controller;

import work.chncyl.base.global.pojo.EntityDtoInt;
import work.chncyl.base.global.result.ApiResult;
import work.chncyl.system.dictionary.dto.input.*;
import work.chncyl.system.dictionary.dto.output.DictionaryTreeNode;
import work.chncyl.system.dictionary.dto.output.DictionaryTypeInfo;
import work.chncyl.system.dictionary.entity.Dictionary;
import work.chncyl.system.dictionary.service.DictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dictionary")
@Api(tags = "数据字典")
@RequiredArgsConstructor
public class DictionaryController {
    private final DictionaryService service;

    @ApiOperation("增加数据字典")
    @PostMapping("/add")
    public ApiResult<Integer> add(@RequestBody DictionaryAddInput input) {
        return ApiResult.success(service.addDictionary(input));
    }

    @ApiOperation("删除数据字典")
    @PostMapping("/delete")
    public ApiResult<Boolean> delete(@RequestBody EntityDtoInt id) {
        return ApiResult.success(service.delete(id.getId()));
    }

    @ApiOperation("修改数据字典")
    @PostMapping("/update")
    public ApiResult<Boolean> update(@RequestBody DictionaryUpdateInput input) {
        return ApiResult.success(service.updateDictionary(input));
    }

    @ApiOperation("获取数据字典")
    @GetMapping("/getDictionary")
    public ApiResult<List<Dictionary>> getDictionary(DictionarySearchInfo info) {
        return ApiResult.success(service.getDictionaryDetail(info));
    }

    @ApiOperation("获取数据字典列表(树结构)")
    @GetMapping("/dictionaryTree")
    public ApiResult<Map<String, List<DictionaryTreeNode>>> dictionaryTree(DictionaryTreeInput input) {
        return ApiResult.success(service.dictionaryTree(input));
    }

    @ApiOperation("获取数据字典列表(树结构)")
    @GetMapping("/getDictionaryType")
    public ApiResult<List<DictionaryTypeInfo>> getDictionaryType(DictionaryTypeSearch input) {
        return ApiResult.success(service.getDictionaryType(input));
    }
}
