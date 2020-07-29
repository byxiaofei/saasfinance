package com.sinosoft.service.account;

import java.util.Date;
import java.util.List;

public interface QueryTagJournalService {

    public List<?> qryVoucherTag(String yearMonthDateBegin , String yearMonthDateEnd ,String DateStart , String DateStop , String tagCode);
    public List<?> queryTagJournal(String value);
}
