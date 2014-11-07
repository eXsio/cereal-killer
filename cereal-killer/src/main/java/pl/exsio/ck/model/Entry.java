package pl.exsio.ck.model;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author exsio
 */
public class Entry {

    private Integer id;

    private String serialNo;

    private String supplier;

    private String buyInvoiceNo;

    private String recipient;

    private Date supplyDate;

    private Date sellDate;

    private String sellInvoiceNo;

    private Date importedAt;
    
    public Entry() {
        this.importedAt = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getBuyInvoiceNo() {
        return buyInvoiceNo;
    }

    public void setBuyInvoiceNo(String buyInvoiceNo) {
        this.buyInvoiceNo = buyInvoiceNo;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Date getSupplyDate() {
        return supplyDate;
    }

    public void setSupplyDate(Date supplyDate) {
        this.supplyDate = supplyDate;
    }

    public Date getSellDate() {
        return sellDate;
    }

    public void setSellDate(Date sellDate) {
        this.sellDate = sellDate;
    }

    public String getSellInvoiceNo() {
        return sellInvoiceNo;
    }

    public void setSellInvoiceNo(String sellInvoiceNo) {
        this.sellInvoiceNo = sellInvoiceNo;
    }

    public Date getImportedAt() {
        return importedAt;
    }

    public void setImportedAt(Date importedAt) {
        this.importedAt = importedAt;
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
        final Entry other = (Entry) obj;
        return Objects.equals(this.id, other.id);
    }

}
