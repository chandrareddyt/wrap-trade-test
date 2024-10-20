package com.trade.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HistoricalQuotesDto extends BaseDto{

	@NotNull(message = "Symbol is required")
	private String symbol;
	@NotNull(message = "Interval is required")
    private String interval;
	@NotNull(message = "To Date is required")
    private String toDate;
	@NotNull(message = "From Date is required")
    private String fromDate;
	
}
