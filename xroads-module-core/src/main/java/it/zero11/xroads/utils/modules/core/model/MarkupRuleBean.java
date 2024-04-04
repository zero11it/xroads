package it.zero11.xroads.utils.modules.core.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Objects;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MarkupRuleBean implements Serializable, Comparable<MarkupRuleBean>{

	private static final long serialVersionUID = 1L;

	@XmlElement
	private String id;

    @XmlElement
    private JsonNode properties;

    @XmlElement
    private String basePriceType;

    @XmlElement
    private BigDecimal markupPercentage;

    @XmlElement
    private BigDecimal minCostMarkupPercentage;

    @XmlElement
    private BigDecimal markupFixed;

    @XmlElement
    private MarkupRoundType roundType;

	public boolean isDefaultRule() {
		return properties == null || properties.size() == 0;
	}

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public JsonNode getProperties() {
        return properties;
    }

    public void setProperties(JsonNode properties) {
        this.properties = properties;
    }

    public String getBasePriceType() {
        return basePriceType;
    }

    public void setBasePriceType(String basePriceType) {
        this.basePriceType = basePriceType;
    }

    public BigDecimal getMarkupPercentage() {
        return markupPercentage;
    }

    public void setMarkupPercentage(BigDecimal markupPercentage) {
        this.markupPercentage = markupPercentage;
    }

    public BigDecimal getMinCostMarkupPercentage() {
        return minCostMarkupPercentage;
    }

    public void setMinCostMarkupPercentage(BigDecimal minCostMarkupPercentage) {
        this.minCostMarkupPercentage = minCostMarkupPercentage;
    }

    public BigDecimal getMarkupFixed() {
        return markupFixed;
    }

    public void setMarkupFixed(BigDecimal markupFixed) {
        this.markupFixed = markupFixed;
    }

    public MarkupRoundType getRoundType() {
        return roundType;
    }

    public void setRoundType(MarkupRoundType roundType) {
        this.roundType = roundType;
    }

	@Override
	public int compareTo(MarkupRuleBean o) {
		int mine = 0;
		
		if (properties == null)
			return 1;
		if (o.properties == null)
			return -1;
		
		for (Iterator<Entry<String, JsonNode>> iterator = properties.fields(); iterator.hasNext();) {
			Entry<String, JsonNode> field = iterator.next();
			mine += 100;
			mine += field.getValue().size();			
		}
		
		int other = 0;
		
		for (Iterator<Entry<String, JsonNode>> iterator = o.properties.fields(); iterator.hasNext();) {
			Entry<String, JsonNode> field = iterator.next();
			other += 100;
			other += field.getValue().size();			
		}
		
		int delta = other - mine;
		if (delta == 0){
			return getId().compareTo(o.getId());
		}else{
			return delta;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(basePriceType, markupFixed, markupPercentage, minCostMarkupPercentage, properties, roundType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MarkupRuleBean other = (MarkupRuleBean) obj;
		return basePriceType == other.basePriceType && Objects.equals(markupFixed, other.markupFixed)
				&& Objects.equals(markupPercentage, other.markupPercentage)
				&& Objects.equals(minCostMarkupPercentage, other.minCostMarkupPercentage)
				&& Objects.equals(properties, other.properties) && roundType == other.roundType;
	}

}