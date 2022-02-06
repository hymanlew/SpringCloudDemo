package com.hyman.common.feign;

import com.hyman.common.fallback.IBaseFeignFallback;
import com.hyman.common.model.dto.base.RoleAuthorityDTO;
import com.hyman.common.model.dto.base.SysCode;
import com.hyman.common.model.dto.driver.CustomerServicePhoneDTO;
import com.hyman.common.model.dto.match.UserDTO;
import com.hyman.common.model.form.base.QuerySpecifiedLowerUserListFORM;
import com.hyman.common.model.form.base.QuerySpecifiedUserListForm;
import com.hyman.common.model.form.order.DictDataListFORM;
import com.hyman.common.model.vo.base.*;
import com.hyman.common.model.vo.driver.UserVO;
import com.hyman.common.msg.Result;
import com.hyman.common.vo.BooleanResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.hyman.common.constant.CommonConstants.JWT_KEY_USER_ID;

@FeignClient(value = "service-base", fallbackFactory = IBaseFeignFallback.class, primary = false)
public interface IBaseFeign {

    /**
     * 根据 type 类型获取字典数据
     */
    @RequestMapping(value = "/base/dict/list", method = RequestMethod.POST)
    Result<List<DictDataVO>> getDictByType(@Validated @RequestBody DictDataListFORM dictDataListFORM);

    /**
     * 提供给前台查询 具体 type 类型的字典数据
     *
     * @param dictType
     * @return
     */
    @RequestMapping(value = "/base/getDictData/list", method = RequestMethod.GET)
    Result<List<DictDataVO>> qryDictByType(@RequestParam("dictType") String dictType);

    @GetMapping("/area/{cityCodes}")
    Result<HashMap<String, String>> getAreaNameByCityCode(@PathVariable("cityCodes") int[] cityCodes);

    @ApiOperation("根据用户Id集合获取用户列表信息")
    @PostMapping("/base/getUserListByIds")
    Result<List<UserBasicInfoVO>> selectUserListByIds(@RequestBody @Validated @NotNull(message = "Ids不能为空") Set<Long> ids);

    @ApiOperation("查询用户下的城市组织")
    @GetMapping("/base/queryUserIdUnderCityOffice")
    Result<OfficeVoWitchSelected> queryUserIdUnderCityOffice(@RequestHeader(JWT_KEY_USER_ID) Long userId);

    @GetMapping("/base/queryOfficeById")
    @ApiOperation("根据组织Id查询组织信息")
    Result<OfficeVO> queryOfficeById(@RequestParam("officeId") @Validated Integer officeId);
}