package it.zero11.xroads.utils.modules.core.utils;

import org.json.JSONException;
import org.json.JSONObject;

import it.zero11.xroads.utils.modules.core.XRoadsCoreModule;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;
import it.zero11.xroads.utils.modules.core.model.ParamType;

public class ClusterSettingsUtils {
	public static final String POOL_SIZE = "poolSize";
	public static final String ONLY_NODE_CRON = "onlyNodeCron";
	public static String INSTANCE_NAME = (System.getProperty("nodename") != null) ? System.getProperty("nodename") : "Default";
	public static String NODE_NAME = (System.getProperty("nodename") != null) ? System.getProperty("nodename") : "Default";
			
	public static JSONObject getNodeSetting() {
		String settingsString = ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.CRON_POOL_SETTING, false);

		JSONObject clusterSettings;
		if (settingsString == null) {
			clusterSettings = new JSONObject();
		}else {
			clusterSettings = new JSONObject(settingsString);
		}
		
		JSONObject mergedSettings = new JSONObject();
		mergedSettings.put(POOL_SIZE, 1);
		mergedSettings.put(ONLY_NODE_CRON, true);
		
		{	
			JSONObject defaultSettings = clusterSettings.optJSONObject("default");
			if (defaultSettings != null) {
				deepMerge(defaultSettings, mergedSettings);
			}
		}
		{
			JSONObject nodeSettings = clusterSettings.optJSONObject(NODE_NAME);
			if (nodeSettings != null) {
				deepMerge(nodeSettings, mergedSettings);
			}
		}
		{	
			JSONObject instanceSettings = clusterSettings.optJSONObject(INSTANCE_NAME);
			if (instanceSettings != null) {
				deepMerge(instanceSettings, mergedSettings);
			}
		}
		
		return mergedSettings;
	}
	
	private static void deepMerge(JSONObject source, JSONObject target) throws JSONException {
	    for (String key: JSONObject.getNames(source)) {
            Object value = source.get(key);
            if (!target.has(key)) {
                target.put(key, value);
            } else {
                if (value instanceof JSONObject) {
                    JSONObject valueJson = (JSONObject)value;
                    deepMerge(valueJson, target.getJSONObject(key));
                } else {
                    target.put(key, value);
                }
            }
	    }
	}
}
