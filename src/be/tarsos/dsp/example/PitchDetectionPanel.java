package be.tarsos.dsp.example;

import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class PitchDetectionPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5107785666165487335L;

	private PitchEstimationAlgorithm algo;
	
	public PitchDetectionPanel(ActionListener algoChangedListener){
		super(new GridLayout(0,1));
		setBorder(new TitledBorder("2. Choose a pitch detection algorithm"));
		ButtonGroup group = new ButtonGroup();
		algo = PitchEstimationAlgorithm.YIN;
		for (PitchEstimationAlgorithm value : PitchEstimationAlgorithm.values()) {
			JRadioButton button = new JRadioButton();
			button.setText(value.toString());
			add(button);
			group.add(button);
			button.setSelected(value == algo);
			button.setActionCommand(value.name());
			button.addActionListener(algoChangedListener);
		}
	}
}