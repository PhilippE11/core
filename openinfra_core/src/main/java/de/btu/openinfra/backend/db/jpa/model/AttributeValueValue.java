package de.btu.openinfra.backend.db.jpa.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the attribute_value_value database table.
 * 
 */
@Entity
@Table(name="attribute_value_value")
@NamedQuery(name="AttributeValueValue.findAll", query="SELECT a FROM AttributeValueValue a")
public class AttributeValueValue implements Serializable, OpenInfraModelObject {
	private static final long serialVersionUID = 1L;

	@Id
	private UUID id;

	//bi-directional many-to-one association to AttributeTypeToAttributeTypeGroup
	@ManyToOne
	@JoinColumn(name="attribute_type_to_attribute_type_group_id")
	private AttributeTypeToAttributeTypeGroup attributeTypeToAttributeTypeGroup;

	//bi-directional many-to-one association to PtFreeText
	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name="value")
	private PtFreeText ptFreeText;

	//bi-directional many-to-one association to TopicInstance
	@ManyToOne
	@JoinColumn(name="topic_instance_id")
	private TopicInstance topicInstance;

	public AttributeValueValue() {
	}

	public UUID getId() {
		return this.id;
	}

	@Override
	public void setId(UUID id) {
		this.id = id;
	}

	public AttributeTypeToAttributeTypeGroup getAttributeTypeToAttributeTypeGroup() {
		return this.attributeTypeToAttributeTypeGroup;
	}

	public void setAttributeTypeToAttributeTypeGroup(AttributeTypeToAttributeTypeGroup attributeTypeToAttributeTypeGroup) {
		this.attributeTypeToAttributeTypeGroup = attributeTypeToAttributeTypeGroup;
	}

	public PtFreeText getPtFreeText() {
		return this.ptFreeText;
	}

	public void setPtFreeText(PtFreeText ptFreeText) {
		this.ptFreeText = ptFreeText;
	}

	public TopicInstance getTopicInstance() {
		return this.topicInstance;
	}

	public void setTopicInstance(TopicInstance topicInstance) {
		this.topicInstance = topicInstance;
	}

}