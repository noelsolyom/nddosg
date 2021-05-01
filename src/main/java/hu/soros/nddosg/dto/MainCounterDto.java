package hu.soros.nddosg.dto;

public class MainCounterDto {

	private String data;

	public MainCounterDto() {
		super();
	}

	public MainCounterDto(String data) {
		super();
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "MainCounterDto [data=" + data + "]";
	}

}
