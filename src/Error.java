
public class Error {

	private String _id;
	private String type;
	private String message;
	private String detail;
	
	public Error(){}
	
	public Error(String type, String message, String detail){
		this.type = type;
		this.message = message;
		this.detail = detail;
	}
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	
	
}
