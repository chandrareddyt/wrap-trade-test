package com.trade.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlReportDto extends BaseDto {

	@NotNull(message = "From Date is required")
	private String fromDate;
	@NotNull(message = "To Date is required")
	private String toDate;
	@NotNull(message = "Segment is required")
	private String segment;
	@NotNull(message = "Financial year is required in the format YYYY. For Example 2023-2024 as 2324")
	private String financialYear;
	@NotNull(message = "Page Number is required")
	private int pageNumber;
	@NotNull(message = "Page Size is required")
	private int pageSize;

}
