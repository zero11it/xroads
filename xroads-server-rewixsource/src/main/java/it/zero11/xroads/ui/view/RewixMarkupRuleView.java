package it.zero11.xroads.ui.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import it.zero11.xroads.model.IParamType;
import it.zero11.xroads.modules.rewixsource.XRoadsRewixSourceModule;
import it.zero11.xroads.modules.rewixsource.api.model.TagType;
import it.zero11.xroads.modules.rewixsource.cron.RewixSourceQuantitySyncCron;
import it.zero11.xroads.modules.rewixsource.model.RewixMarkupPriceType;
import it.zero11.xroads.modules.rewixsource.model.RewixSourceParamType;
import it.zero11.xroads.modules.rewixsource.service.RewixService;
import it.zero11.xroads.ui.abstractview.AbstractMarkupRuleView;
import it.zero11.xroads.ui.component.TagTokenField;
import it.zero11.xroads.ui.i18n.UITranslation;
import it.zero11.xroads.ui.utils.RewixUIUtils;
import it.zero11.xroads.utils.modules.core.model.MarkupRuleBean;

public class RewixMarkupRuleView extends AbstractMarkupRuleView<XRoadsRewixSourceModule> {

	private static final long serialVersionUID = 1L;

	private RewixService rewixService;

	public RewixMarkupRuleView() {
		super(XRoadsRewixSourceModule.class);
		this.rewixService = new RewixService();
	}

	@Override
	protected List<TagTokenField> getTagTokensFields(ObjectNode value) {
		List<TagTokenField> tagTokenFieldsList = new ArrayList<TagTokenField>();
		
		Map<String, String> tagNames = getTagNames(xRoadsModule);

		String brandTagIdStr = String.valueOf(TagType.TAG_BRAND.tagId);
		TagTokenField tag1TokenField = new TagTokenField(brandTagIdStr, getTagValues(xRoadsModule, brandTagIdStr),
				getTranslation(UITranslation.MARKUP_RULES_ADD_BRAND));
		tag1TokenField.setLabel(tagNames.getOrDefault(brandTagIdStr, TagType.TAG_BRAND.tagName));
		tagTokenFieldsList.add(tag1TokenField);

		String colorTagIdStr = String.valueOf(TagType.TAG_COLOR.tagId);
		TagTokenField tag13TokenField = new TagTokenField(colorTagIdStr, getTagValues(xRoadsModule, colorTagIdStr),
				getTranslation(UITranslation.MARKUP_RULES_ADD_COLOR));
		tag13TokenField.setLabel(tagNames.getOrDefault(colorTagIdStr, TagType.TAG_COLOR.tagName));
		tagTokenFieldsList.add(tag13TokenField);

		String categoryTagIdStr = String.valueOf(TagType.TAG_CATEGORY.tagId);
		TagTokenField tag4TokenField = new TagTokenField(categoryTagIdStr, getTagValues(xRoadsModule, categoryTagIdStr),
				getTranslation(UITranslation.MARKUP_RULES_ADD_CATEGORY));
		tag4TokenField.setLabel(tagNames.getOrDefault(categoryTagIdStr, TagType.TAG_CATEGORY.tagName));
		tagTokenFieldsList.add(tag4TokenField);

		String subcategoryTagIdStr = String.valueOf(TagType.TAG_SUBCATEGORY.tagId);
		TagTokenField tag5TokenField = new TagTokenField(subcategoryTagIdStr, getTagValues(xRoadsModule, subcategoryTagIdStr),
				getTranslation(UITranslation.MARKUP_RULES_ADD_SUBCATEGORY));
		tag5TokenField.setLabel(tagNames.getOrDefault(subcategoryTagIdStr, TagType.TAG_SUBCATEGORY.tagName));
		tagTokenFieldsList.add(tag5TokenField);

		String genderTagIdStr = String.valueOf(TagType.TAG_GENDER.tagId);
		TagTokenField tag26TokenField = new TagTokenField(genderTagIdStr, getTagValues(xRoadsModule, genderTagIdStr),
				getTranslation(UITranslation.MARKUP_RULES_ADD_GENDER));
		tag26TokenField.setLabel(tagNames.getOrDefault(genderTagIdStr, TagType.TAG_GENDER.tagName));
		tagTokenFieldsList.add(tag26TokenField);

		String seasonTagIdStr = String.valueOf(TagType.TAG_SEASON.tagId);
		TagTokenField tag11TokenField = new TagTokenField(seasonTagIdStr, getTagValues(xRoadsModule, seasonTagIdStr),
				getTranslation(UITranslation.MARKUP_RULES_ADD_SEASON));
		tag11TokenField.setLabel(tagNames.getOrDefault(seasonTagIdStr, TagType.TAG_SEASON.tagName));
		tagTokenFieldsList.add(tag11TokenField);

		return tagTokenFieldsList;
	}

	@Override
	public Map<String, String> getTagNames(XRoadsRewixSourceModule xRoadsModule) {
		return rewixService.getTagNames(xRoadsModule);
	}

	@Override
	public Map<String, String> getTagValues(XRoadsRewixSourceModule xRoadsModule, String tagId) {
		return rewixService.getTagValues(xRoadsModule, tagId);
	}

	@Override
	public Map<String, String> getFilteredTagValues(XRoadsRewixSourceModule xRoadsModule, String tagId,
			Map<String, List<String>> filterNode) {
		return rewixService.getFilteredTagValues(xRoadsModule, tagId, filterNode);
	}

	@Override
	protected Map<String, String> getMarkupPriceType() {
		return List.of(RewixMarkupPriceType.values()).stream()
				.collect(Collectors.toMap(rwxMarkupPriceType -> rwxMarkupPriceType.value,
						RewixUIUtils::getMarkupPriceTypeLabel));
	}

	@Override
	protected IParamType getMarkupRulesParameter() {
		return RewixSourceParamType.MARKUP_RULES;
	}

	@Override
	protected void onDeleteMarkupRule(MarkupRuleBean markupRuleBean) {
		xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.CATALOG_LASTQUANTITYREF, "force-full-sync");
		xRoadsModule.getXRoadsCoreService().addScheduleNowIfNotScheduled(RewixSourceQuantitySyncCron.class, xRoadsModule);
	}

	@Override
	protected void onSaveMarkupRule(MarkupRuleBean markupRuleBean) {
		xRoadsModule.getXRoadsCoreService().updateParam(xRoadsModule, RewixSourceParamType.CATALOG_LASTQUANTITYREF, "force-full-sync");
		xRoadsModule.getXRoadsCoreService().addScheduleNowIfNotScheduled(RewixSourceQuantitySyncCron.class, xRoadsModule);
	}

}