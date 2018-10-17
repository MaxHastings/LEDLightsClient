package be.tarsos.dsp.example;

public class AutoBand {

	private long prevTime = System.currentTimeMillis();
	
	private long currTime = System.currentTimeMillis();
	
	private long waitTime = 1500;
	
	private float minValueSeen = Float.MAX_VALUE;
	
	private float maxValueSeen = Float.MIN_VALUE;
	
	private float lastMinSent = 0;
	
	private float lastMaxSent = 0;
	
	private float lowerSpacing = -0.05f;
	
	private float upperSpacing = 0.1f;

	private OnUpdateBands listener;
	
	public AutoBand(OnUpdateBands listener) {
		this.listener = listener;
	}

	public int getLowerSpacing() {
		return Math.round(lowerSpacing * 100);
	}

	public void setLowerSpacing(int spacing) {
		this.lowerSpacing = (float)spacing / 100;
	}

	public int getUpperSpacing() {
		return Math.round(upperSpacing * 100);
	}

	public void setUpperSpacing(int spacing) {
		this.upperSpacing = (float)spacing / 100;
	}



	public void newVolume(float volume){
		if(volume < minValueSeen){
			minValueSeen = volume;
		}
		if(volume > maxValueSeen){
			maxValueSeen = volume;
		}
		
		currTime = System.currentTimeMillis();
		if(currTime - prevTime > waitTime){
			lastMinSent = (float) ((lastMinSent * 0.5) + (minValueSeen * 0.5));
			lastMaxSent = (float) ((lastMaxSent * 0.5) + (maxValueSeen * 0.5));
			
			listener.updateBands(Math.min(1, lastMaxSent + upperSpacing), Math.max(0, lastMinSent - lowerSpacing), lowerSpacing, upperSpacing);
			reset();
		}
	}
	
	public void reset(){
		minValueSeen = Float.MAX_VALUE;
		maxValueSeen = Float.MIN_VALUE;
		prevTime = System.currentTimeMillis();
	}
	
	public interface OnUpdateBands{
		void updateBands(float max, float min, float lowerSpacing, float upperSpacing);
	}
	
}
