package de.btu.openinfra.backend.db.jpa.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the attribute_value_domain database table.
 *
 */
@Entity
@Table(name="attribute_value_domain")
@NamedQueries({
    @NamedQuery(name="AttributeValueDomain.findAll",
        query="SELECT a FROM AttributeValueDomain a"),
    @NamedQuery(name="AttributeValueDomain.getSuggestion",
        query="SELECT a FROM AttributeValueDomain a "
                + "JOIN a.attributeTypeToAttributeTypeGroup.attributeType at "
                + "JOIN a.attributeTypeToAttributeTypeGroup"
                    + ".attributeTypeGroupToTopicCharacteristic"
                    + ".topicCharacteristic tc "
                + "JOIN a.valueListValue.ptFreeText2"
                    + ".localizedCharacterStrings l "
                + "WHERE at.id = :atId AND "
                    + "tc.id = :tcId AND "
                    + "(l.ptLocale = :qLocale OR l.ptLocale = :xLocale) AND "
                    + "LOWER(l.freeText) LIKE LOWER(:qString) "
                + "ORDER BY LOWER(l.freeText)")
})
public class AttributeValueDomain extends OpenInfraModelObject
    implements Serializable {
	private static final long serialVersionUID = 1L;

	//bi-directional many-to-one association to AttributeTypeToAttributeTypeGroup
	@ManyToOne
	@JoinColumn(name="attribute_type_to_attribute_type_group_id")
	private AttributeTypeToAttributeTypeGroup attributeTypeToAttributeTypeGroup;

	//bi-directional many-to-one association to TopicInstance
	@ManyToOne
	@JoinColumn(name="topic_instance_id")
	private TopicInstance topicInstance;

	//bi-directional many-to-one association to ValueListValue
	@ManyToOne
	@JoinColumn(name="domain")
	private ValueListValue valueListValue;

	public AttributeValueDomain() {
	}

	public AttributeTypeToAttributeTypeGroup getAttributeTypeToAttributeTypeGroup() {
		return this.attributeTypeToAttributeTypeGroup;
	}

	public void setAttributeTypeToAttributeTypeGroup(AttributeTypeToAttributeTypeGroup attributeTypeToAttributeTypeGroup) {
		this.attributeTypeToAttributeTypeGroup = attributeTypeToAttributeTypeGroup;
	}

	public TopicInstance getTopicInstance() {
		return this.topicInstance;
	}

	public void setTopicInstance(TopicInstance topicInstance) {
		this.topicInstance = topicInstance;
	}

	public ValueListValue getValueListValue() {
		return this.valueListValue;
	}

	public void setValueListValue(ValueListValue valueListValue) {
		this.valueListValue = valueListValue;
	}

}