package dk.scicomp.ledcontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    // UI members
    private SeekBar redBar, redBar2;
    private SeekBar greenBar, greenBar2;
    private SeekBar blueBar, blueBar2;
    private Switch onOffSwitch;
    private Button button;

    // Non UI members
    private SendUpdatesWorker updater = new SendUpdatesWorker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(updater).start();
        setContentView(R.layout.activity_main);

        redBar = findViewById(R.id.redBar);
        redBar.setOnSeekBarChangeListener(this);

        greenBar = findViewById(R.id.greenBar);
        greenBar.setOnSeekBarChangeListener(this);

        blueBar = findViewById(R.id.blueBar);
        blueBar.setOnSeekBarChangeListener(this);

        redBar2 = findViewById(R.id.redBar2);
        redBar2.setOnSeekBarChangeListener(this);

        greenBar2 = findViewById(R.id.greenBar2);
        greenBar2.setOnSeekBarChangeListener(this);

        blueBar2 = findViewById(R.id.blueBar2);
        blueBar2.setOnSeekBarChangeListener(this);

        onOffSwitch = findViewById(R.id.switch1);
        onOffSwitch.setOnCheckedChangeListener(this);

        button = findViewById(R.id.button);
        button.setOnClickListener(new SearchClickHandler(25000,
                new Function<String, String>() {
                    @Override
                    public String apply(String input) {
                        final String val = input;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button.setText(val);
                            }
                        });
                        return input;
                    }
                }));
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        byte red, green, blue;
        red = (byte) Math.min(redBar.getProgress(), 255);
        green = (byte) Math.min(greenBar.getProgress(), 255);
        blue = (byte) Math.min(blueBar.getProgress(), 255);
        updater.setColor(red, green, blue);

        red = (byte) Math.min(redBar2.getProgress(), 255);
        green = (byte) Math.min(greenBar2.getProgress(), 255);
        blue = (byte) Math.min(blueBar2.getProgress(), 255);
        updater.setColor2(red, green, blue);

        updater.update();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        updater.setOn(isChecked);
    }

}