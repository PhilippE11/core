package de.btu.openinfra.backend.db.pojos;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import de.btu.openinfra.backend.db.daos.MetaDataDao;
import de.btu.openinfra.backend.db.jpa.model.OpenInfraModelObject;

@XmlRootElement
public class TopicCharacteristicPojo extends OpenInfraMetaDataPojo {

    private PtFreeTextPojo descriptions;
    private ValueListValuePojo topic;
    private UUID projectId;

    /* Default constructor */
    public TopicCharacteristicPojo() {
    }

    /* Constructor that will set the id, trid and meta data automatically */
    public TopicCharacteristicPojo(OpenInfraModelObject modelObject, MetaDataDao mdDao) {
        super(modelObject, mdDao);
    }

    public PtFreeTextPojo getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(PtFreeTextPojo descriptions) {
        this.descriptions = descriptions;
    }

    public ValueListValuePojo getTopic() {
        return topic;
    }

    public void setTopic(ValueListValuePojo topic) {
        this.topic = topic;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    @Override
    protected void makePrimerHelper() {
        descriptions = new PtFreeTextPojo();
        descriptions.makePrimerHelper();
        topic = new ValueListValuePojo();
        topic.makePrimerHelper();
        projectId = null;
    }

}
