package pl.exsio.ck.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author exsio
 */
public class EntryImpl implements Entry {

    private Integer id;

    private String serialNo;

    private String supplier;

    private String buyInvoiceNo;

    private String recipient;

    private Date supplyDate;

    private Date sellDate;

    private String sellInvoiceNo;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getSerialNo() {
        return serialNo;
    }

    @Override
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    @Override
    public String getSupplier() {
        return supplier;
    }

    @Override
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    @Override
    public String getBuyInvoiceNo() {
        return buyInvoiceNo;
    }

    @Override
    public void setBuyInvoiceNo(String buyInvoiceNo) {
        this.buyInvoiceNo = buyInvoiceNo;
    }

    @Override
    public String getRecipient() {
        return recipient;
    }

    @Override
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public Date getSupplyDate() {
        return supplyDate;
    }

    @Override
    public void setSupplyDate(Date supplyDate) {
        this.supplyDate = supplyDate;
    }

    @Override
    public Date getSellDate() {
        return sellDate;
    }

    @Override
    public void setSellDate(Date sellDate) {
        this.sellDate = sellDate;
    }

    @Override
    public String getSellInvoiceNo() {
        return sellInvoiceNo;
    }

    @Override
    public void setSellInvoiceNo(String sellInvoiceNo) {
        this.sellInvoiceNo = sellInvoiceNo;
    }

    @Override
    public String toString() {
        return this.serialNo + " (id: " + this.id + ")";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EntryImpl other = (EntryImpl) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public boolean isDataFilled() {
        return this.buyInvoiceNo != null
                && this.recipient != null
                && this.sellDate != null
                && this.sellInvoiceNo != null
                && this.supplier != null
                && this.supplyDate != null;
    }

    @Override
    public String getDigest() {
        if (this.isDataFilled()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
            return DigestUtils.md5Hex(
                    this.buyInvoiceNo.trim().toLowerCase()
                    + this.recipient.trim().toLowerCase()
                    + this.sellInvoiceNo.trim().toLowerCase()
                    + this.supplier.trim().toLowerCase()
                    + sdf.format(this.sellDate)
                    + sdf.format(this.supplyDate));
        } else {
            return null;
        }
    }

}
