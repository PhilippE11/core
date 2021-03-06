package de.btu.openinfra.backend.db.pojos;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import de.btu.openinfra.backend.db.jpa.model.OpenInfraModelObject;

@XmlRootElement
public class MetaDataPojo extends OpenInfraPojo {

    private UUID objectId;
    private String tableName;
    private String pkColumn;
    private String data;

    /* Default constructor */
    public MetaDataPojo() {
    }

    /* Constructor that will set the id and trid data automatically */
    public MetaDataPojo(OpenInfraModelObject modelObject) {
        super(modelObject);
    }

    public UUID getObjectId() {
        return objectId;
    }

    public void setObjectId(UUID objectId) {
        this.objectId = objectId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPkColumn() {
        return pkColumn;
    }

    public void setPkColumn(String pkColumn) {
        this.pkColumn = pkColumn;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
