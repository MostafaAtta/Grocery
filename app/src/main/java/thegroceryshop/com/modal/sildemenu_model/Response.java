package thegroceryshop.com.modal.sildemenu_model;

import com.google.gson.annotations.Expose;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Response{

	@SerializedName("status_code")
	@Expose
	private int statusCode;

	@SerializedName("data")
	@Expose
	private Data data;

	@SerializedName("success_message")
	@Expose
	private String successMessage;

	@SerializedName("status")
	@Expose
	private String status;

	public void setStatusCode(int statusCode){
		this.statusCode = statusCode;
	}

	public int getStatusCode(){
		return statusCode;
	}

	public void setData(Data data){
		this.data = data;
	}

	public Data getData(){
		return data;
	}

	public void setSuccessMessage(String successMessage){
		this.successMessage = successMessage;
	}

	public String getSuccessMessage(){
		return successMessage;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"Response{" + 
			"status_code = '" + statusCode + '\'' + 
			",data = '" + data + '\'' + 
			",success_message = '" + successMessage + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}