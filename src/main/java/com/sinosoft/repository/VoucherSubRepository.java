package com.sinosoft.repository;

import com.sinosoft.domain.account.AccMainVoucher;
import com.sinosoft.domain.account.AccMainVoucherId;
import com.sinosoft.domain.account.AccSubVoucher;
import com.sinosoft.domain.account.AccSubVoucherId;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherSubRepository extends  BaseRepository<AccSubVoucher, AccSubVoucherId> {

}
