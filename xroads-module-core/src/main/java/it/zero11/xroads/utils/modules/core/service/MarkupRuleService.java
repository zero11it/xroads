package it.zero11.xroads.utils.modules.core.service;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import it.zero11.xroads.model.IParamType;
import it.zero11.xroads.modules.AbstractXRoadsModule;
import it.zero11.xroads.utils.XRoadsUtils;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;
import it.zero11.xroads.utils.modules.core.model.MarkupRuleBean;
import it.zero11.xroads.utils.modules.core.model.MarkupRuleType;

public class MarkupRuleService<T extends AbstractXRoadsModule> {

	public Map<MarkupRuleType, TreeSet<MarkupRuleBean>> getMarkupByModule(T xRoadsModule, IParamType param) {
		JsonNode martkupBeansJson = xRoadsModule.getXRoadsCoreService().getParameterAsJsonNode(xRoadsModule, param);
		Map<MarkupRuleType, TreeSet<MarkupRuleBean>> markupBeans = XRoadsUtils.OBJECT_MAPPER.convertValue(martkupBeansJson,
				new TypeReference<Map<MarkupRuleType, TreeSet<MarkupRuleBean>>>() {
				});
		return markupBeans;
	}

	public TreeSet<MarkupRuleBean> getMarkupByModuleAndRuleType(T xRoadsModule, MarkupRuleType markupRuleType, IParamType param) {
		return getMarkupByModule(xRoadsModule, param).get(markupRuleType);
	}

	public void updateModuleMarkup(T xRoadsModule, IParamType param, String markupJsonString) {
		ParamDao.getInstance().updateParam(xRoadsModule, param, markupJsonString);
	}

	public void saveMarkupRule(MarkupRuleBean markupRule, T xRoadsModule,  MarkupRuleType markupRuleType, IParamType param) {
		Map<MarkupRuleType, TreeSet<MarkupRuleBean>> markupRules = getMarkupByModule(xRoadsModule, param);
		TreeSet<MarkupRuleBean> markupRulesTypeSet = markupRules.get(markupRuleType);
		if (markupRule.getId() != null) {
			boolean changed = false;
			for (MarkupRuleBean markupRuleBean : markupRulesTypeSet) {
				if (markupRuleBean.getId().equals(markupRule.getId())) {
					markupRulesTypeSet.remove(markupRuleBean);
					markupRulesTypeSet.add(markupRule);
					changed = true;
					break;
				}
			}
			if (!changed) {
				markupRulesTypeSet.add(markupRule);
			}
		} else {
			markupRule.setId(UUID.randomUUID().toString());
			markupRulesTypeSet.add(markupRule);
		}
		checkDefaultRuleExists(markupRulesTypeSet);
		updateModuleMarkup(xRoadsModule, param, convertMarkupRulesToJsonString(markupRules));
	}

	public void deleteMarkupRule(MarkupRuleBean markupRule, T xRoadsModule, MarkupRuleType markupRuleType, IParamType param) {
		Map<MarkupRuleType, TreeSet<MarkupRuleBean>> markupRules = getMarkupByModule(xRoadsModule, param);
		TreeSet<MarkupRuleBean> markupRulesTypeSet = markupRules.get(markupRuleType);
		if (markupRule.getId() != null) {
			for (MarkupRuleBean markupRuleBean : markupRulesTypeSet) {
				if (markupRuleBean.getId().equals(markupRule.getId())) {
					markupRulesTypeSet.remove(markupRuleBean);
					break;
				}
			}
		} else {
			throw new RuntimeException("You can't delete unsaved rule!");
		}
		checkDefaultRuleExists(markupRulesTypeSet);
		updateModuleMarkup(xRoadsModule, param, convertMarkupRulesToJsonString(markupRules));
	}

	private void checkDefaultRuleExists(SortedSet<MarkupRuleBean> rules) {
		int defaultRuleCount = 0;
		for (MarkupRuleBean rule : rules) {
			if (rule.isDefaultRule()) {
				defaultRuleCount++;
			}
		}

		if (defaultRuleCount == 0) {
			throw new IllegalArgumentException("A default rule is required");
		} else if (defaultRuleCount > 1) {
			throw new IllegalArgumentException("Only one default rule is allowed");
		}
	}

	private String convertMarkupRulesToJsonString(Map<MarkupRuleType, TreeSet<MarkupRuleBean>> markupRules) {
		try {
			return XRoadsUtils.OBJECT_MAPPER.writeValueAsString(markupRules);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

}
