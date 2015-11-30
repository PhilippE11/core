package de.btu.openinfra.backend.db.jpa.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the attribute_value_value database table.
 *
 */
@Entity
@Table(name="attribute_value_value")
@NamedQueries({
    @NamedQuery(name="AttributeValueValue.findAll",
        query="SELECT a FROM AttributeValueValue a"),
    @NamedQuery(name="AttributeValueValue.getSuggestion",
        query="SELECT a FROM AttributeValueValue a "
                + "JOIN a.attributeTypeToAttributeTypeGroup.attributeType at "
                + "JOIN a.attributeTypeToAttributeTypeGroup"
                    + ".attributeTypeGroupToTopicCharacteristic"
                    + ".topicCharacteristic tc "
                + "JOIN a.ptFreeText.localizedCharacterStrings l "
                + "WHERE at.id = :atId AND "
                    + "tc.id = :tcId AND "
                    + "(l.ptLocale = :qLocale OR l.ptLocale = :xLocale) AND "
                    + "LOWER(l.freeText) LIKE LOWER(:qString) "
                + "ORDER BY LOWER(l.freeText)")
})
public class AttributeValueValue extends OpenInfraModelObject
    implements Serializable {
	private static final long serialVersionUID = 1L;

	//bi-directional many-to-one association to AttributeTypeToAttributeTypeGroup
	@ManyToOne
	@JoinColumn(name="attribute_type_to_attribute_type_group_id")
	private AttributeTypeToAttributeTypeGroup attributeTypeToAttributeTypeGroup;

	//bi-directional many-to-one association to PtFreeText
	@ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH})
	@JoinColumn(name="value")
	private PtFreeText ptFreeText;

	//bi-directional many-to-one association to TopicInstance
	@ManyToOne
	@JoinColumn(name="topic_instance_id")
	private TopicInstance topicInstance;

	public AttributeValueValue() {
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