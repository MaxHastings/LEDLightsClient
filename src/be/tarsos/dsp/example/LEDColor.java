package be.tarsos.dsp.example;

import org.json.simple.JSONObject;

public class LEDColor {
	
	private int red;
	
	private int green;
	
	private int blue;
	
	public LEDColor(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public String toJSONString(){
	    JSONObject obj = new JSONObject();
	    obj.put("cmd", "color");
	    obj.put("red", red);
	    obj.put("green", green);
	    obj.put("blue", blue);
		return obj.toJSONString();
	}
	
	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public static LEDColor blendColors(float weight, LEDColor color1, LEDColor color2){
		//System.out.println("Weight: " + weight);
		int red = Math.round(color1.getRed() * weight + color2.getRed() * (1 - weight));
		int green = Math.round(color1.getGreen() * weight + color2.getGreen() * (1 - weight));
		int blue = Math.round(color1.getBlue() * weight + color2.getBlue() * (1 - weight));
		return new LEDColor(red, green, blue);
	}
	
    // Overriding equals() to compare two Complex objects 
    @Override
    public boolean equals(Object o) { 
  
        // If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        } 
  
        /* Check if o is an instance of Complex or not 
          "null instanceof [type]" also returns false */
        if (!(o instanceof LEDColor)) { 
            return false; 
        } 
          
        // typecast o to Complex so that we can compare data members  
        LEDColor c = (LEDColor) o; 

        // Compare the data members and return accordingly  
        return red == c.red && green == c.green && blue == c.blue;
    } 

}
