package de.btu.openinfra.backend.db.pojos;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import de.btu.openinfra.backend.db.jpa.model.OpenInfraModelObject;

@XmlRootElement
public class ValueListValuePojo extends OpenInfraPojo {

    private boolean visibility;
    private UUID belongsToValueList;
    private PtFreeTextPojo names;
    private PtFreeTextPojo descriptions;

    /* Default constructor */
    public ValueListValuePojo() {
    }

    /* Constructor that will set the id and trid automatically */
    public ValueListValuePojo(OpenInfraModelObject modelObject) {
        super(modelObject);
    }

    public boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public UUID getBelongsToValueList() {
        return belongsToValueList;
    }

    public void setBelongsToValueList(UUID belongsToValueList) {
        this.belongsToValueList = belongsToValueList;
    }

    public PtFreeTextPojo getNames() {
        return names;
    }

    public void setNames(PtFreeTextPojo names) {
        this.names = names;
    }

    public PtFreeTextPojo getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(PtFreeTextPojo descriptions) {
        this.descriptions = descriptions;
    }

}
