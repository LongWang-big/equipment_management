package com.xxx.equipment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ScrapDTO {

    /** 主键，有值为修改，无值为新增 */
    private Long id;

    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    @NotNull(message = "报损数量不能为空")
    @Min(value = 1, message = "报损数量必须大于0")
    private Integer scrapQuantity;

    @NotBlank(message = "报损原因不能为空")
    private String scrapReason;

    @NotBlank(message = "报损类型不能为空")
    private String scrapType;

    @NotNull(message = "报损日期不能为空")
    private LocalDate scrapDate;

    private String applicant;

    /** 审批操作：APPROVE/REJECT（仅审批时使用） */
    private String action;

    private String approver;
}
