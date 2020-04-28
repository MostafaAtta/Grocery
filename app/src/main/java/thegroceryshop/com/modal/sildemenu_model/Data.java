package thegroceryshop.com.modal.sildemenu_model;

import java.util.List;
import com.google.gson.annotations.Expose;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Data{

	@SerializedName("NestedCategory")
	@Expose
	private List<NestedCategoryItem> nestedCategory;

	@SerializedName("PromotionCategory")
	@Expose
	private List<PromotionCategoryItem> promotionCategory;

	public void setNestedCategory(List<NestedCategoryItem> nestedCategory){
		this.nestedCategory = nestedCategory;
	}

	public List<NestedCategoryItem> getNestedCategory(){
		return nestedCategory;
	}

	public void setPromotionCategory(List<PromotionCategoryItem> promotionCategory){
		this.promotionCategory = promotionCategory;
	}

	public List<PromotionCategoryItem> getPromotionCategory(){
		return promotionCategory;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"nestedCategory = '" + nestedCategory + '\'' + 
			",promotionCategory = '" + promotionCategory + '\'' + 
			"}";
		}
}