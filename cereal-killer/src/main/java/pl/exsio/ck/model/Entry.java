package pl.exsio.ck.model;

import java.util.Date;

/**
 *
 * @author exsio
 */
public interface Entry {

    String getBuyInvoiceNo();

    Integer getId();

    Date getImportedAt();

    String getRecipient();

    Date getSellDate();

    String getSellInvoiceNo();

    String getSerialNo();

    String getSupplier();

    Date getSupplyDate();

    boolean isDataFilled();

    void setBuyInvoiceNo(String buyInvoiceNo);

    void setId(int id);

    void setImportedAt(Date importedAt);

    void setRecipient(String recipient);

    void setSellDate(Date sellDate);

    void setSellInvoiceNo(String sellInvoiceNo);

    void setSerialNo(String serialNo);

    void setSupplier(String supplier);

    void setSupplyDate(Date supplyDate);

}
