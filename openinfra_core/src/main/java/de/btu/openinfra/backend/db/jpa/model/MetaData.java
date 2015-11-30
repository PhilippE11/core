package de.btu.openinfra.backend.db.jpa.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.json.simple.JSONObject;

import de.btu.openinfra.backend.db.converter.PostgresJsonConverter;


/**
 * The persistent class for the meta_data database table.
 *
 */
@Entity
@Table(name="meta_data")
@NamedQueries({
	@NamedQuery(name="MetaData.findAll", query="SELECT m FROM MetaData m"),
	@NamedQuery(name="MetaData.count", query="SELECT COUNT(m) FROM MetaData m"),
	@NamedQuery(
	        name="MetaData.findByObjectId",
	        query="SELECT m FROM MetaData m WHERE "
	                + "m.objectId = :oId"),
	@NamedQuery(
	        name="MetaData.findByTableColumn",
	        query="SELECT m FROM MetaData m WHERE "
	                + "m.objectId = :oId AND "
	                + "m.pkColumn = :column AND "
	                + "m.tableName = :table")
})
public class MetaData extends OpenInfraModelObject implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="object_id")
	private UUID objectId;

	@Convert(converter = PostgresJsonConverter.class)
	private JSONObject data;

	@Column(name="pk_column")
	private String pkColumn;

	@Column(name="table_name")
	private String tableName;

	public MetaData() {
	}

	public UUID getObjectId() {
		return this.objectId;
	}

	public void setObjectId(UUID objectId) {
		this.objectId = objectId;
	}

    public JSONObject getData() {
		return this.data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public String getPkColumn() {
		return this.pkColumn;
	}

	public void setPkColumn(String pkColumn) {
		this.pkColumn = pkColumn;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}