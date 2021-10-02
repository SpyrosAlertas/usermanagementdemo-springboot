package gr.spyrosalertas.usermanagementdemo.entity;

// A Class to be used to provide different JSON Mapping Views for User Entity
public class UserViews {

	public static interface Hidden {
	}

	public static interface Brief {
	}

	public static interface Detailed extends Brief {
	}

}
