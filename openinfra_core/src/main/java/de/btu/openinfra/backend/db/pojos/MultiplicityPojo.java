package de.btu.openinfra.backend.db.pojos;

import javax.xml.bind.annotation.XmlRootElement;

import de.btu.openinfra.backend.db.jpa.model.OpenInfraModelObject;

@XmlRootElement
public class MultiplicityPojo extends OpenInfraPojo {

    private Integer min;
    private Integer max;

    /* Default constructor */
    public MultiplicityPojo() {
    }

    /* Constructor that will set the id and trid automatically */
    public MultiplicityPojo(OpenInfraModelObject modelObject) {
        super(modelObject);
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

}
