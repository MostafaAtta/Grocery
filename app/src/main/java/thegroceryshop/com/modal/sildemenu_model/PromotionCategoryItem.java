package thegroceryshop.com.modal.sildemenu_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class PromotionCategoryItem{

	@SerializedName("image")
	@Expose
	private String image;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("id")
	@Expose
	private String id;

	@SerializedName("app_ican_id")
	@Expose
	private int app_ican_id;

	private boolean isSelected;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public int getApp_ican_id() {
		return app_ican_id;
	}

	public void setApp_ican_id(int app_ican_id) {
		this.app_ican_id = app_ican_id;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	@Override
 	public String toString(){
		return 
			"PromotionCategoryItem{" + 
			"image = '" + image + '\'' + 
			",name = '" + name + '\'' +
			",app_ican_id = '" + app_ican_id + '\'' +
			",id = '" + id + '\'' + 
			"}";
		}
}