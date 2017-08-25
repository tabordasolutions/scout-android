package scout.edu.mit.ll.nics.android.api.tasks;

/**
 * Created by luisgutierrez on 8/23/17.
 */

//This class is a hack that holds the fireline temp data (fireline data can overflow intents.putExtra methods, crashing the app)
public class ReceivedMarkupFeaturesData
{
	static private ReceivedMarkupFeaturesData instance;

	private String[] featuresToAdd;
	private String[] featuresToRemove;

	public static void setFeaturesToAdd(String[] featuresToAdd)
	{
		//If no instance, instantiate
		if(instance == null)
			instance = new ReceivedMarkupFeaturesData();

		instance.featuresToAdd = featuresToAdd;
	}

	public static void setFeaturesToRemove(String[] featuresToRemove)
	{
		//If no instance, instantiate
		if(instance == null)
			instance = new ReceivedMarkupFeaturesData();

		instance.featuresToRemove = featuresToRemove;
	}

	public static String[] getFeaturesToAdd()
	{
		if(instance == null)
			return null;
		return instance.featuresToAdd;
	}

	public static String[] getFeaturesToRemove()
	{
		if(instance == null)
			return null;
		return instance.featuresToRemove;
	}


	public static void clearFeatureData()
	{
		if(instance != null)
		{
			instance.featuresToAdd = null;
			instance.featuresToRemove = null;
		}
	}
}
