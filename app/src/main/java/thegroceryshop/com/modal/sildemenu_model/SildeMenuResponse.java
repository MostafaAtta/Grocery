package thegroceryshop.com.modal.sildemenu_model;

import com.google.gson.annotations.Expose;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class SildeMenuResponse{

	@SerializedName("response")
	@Expose
	private Response response;

	public void setResponse(Response response){
		this.response = response;
	}

	public Response getResponse(){
		return response;
	}

	@Override
 	public String toString(){
		return 
			"SildeMenuResponse{" + 
			"response = '" + response + '\'' + 
			"}";
		}
}