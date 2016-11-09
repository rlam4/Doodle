package com.rlam.doodle;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ImageButton settingButton;
    ImageButton newButton;
    ImageButton brushButton;
    ImageButton paintButton;
    ImageButton opacityButton;
    ImageButton saveButton;
    ImageButton importButton;

    boolean hidden;

    int colorProgress;
    int color;
    int width;
    int opacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        final DoodleView doodleView = (DoodleView) findViewById(R.id.doodleView);

        settingButton = (ImageButton) findViewById(R.id.settingButton);
        newButton = (ImageButton) findViewById(R.id.imageButton6);
        brushButton = (ImageButton) findViewById(R.id.imageButton2);
        paintButton = (ImageButton) findViewById(R.id.imageButton3);
        opacityButton = (ImageButton) findViewById(R.id.imageButton4);
        saveButton = (ImageButton) findViewById(R.id.imageButton5);
        importButton = (ImageButton) findViewById(R.id.imageButton7);

        color = Color.BLACK; // start at black
        colorProgress = 1792; // start at black
        width = 14;
        opacity = 255; //set to max visibility


        hideButtons();

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hidden) {
                    showButtons();
                } else {
                    hideButtons();
                }
            }
        });

        //Start new

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("New Painting");
                dialog.setMessage("Are you sure you want to start a new painting?");

                dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doodleView.startNewPainting();
                        hideButtons();
                    }
                });

                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                dialog.show();
            }
        });

        //Brush width
        brushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder widthDialog = new AlertDialog.Builder(MainActivity.this);
                widthDialog.setTitle("Brush Size");
                widthDialog.setMessage("Set your brush size:");

                final SeekBar widthSeek = new SeekBar(MainActivity.this);
                widthSeek.setMax(100);
                widthSeek.setKeyProgressIncrement(1);
                widthSeek.setMinimumHeight(100);

                widthSeek.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                widthSeek.getProgressDrawable().setAlpha(opacity);

                widthSeek.setProgress(width);
                ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
                thumb.setIntrinsicHeight(width);
                thumb.setIntrinsicWidth(width);
                widthSeek.setThumb(thumb);

                widthSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
                        thumb.setIntrinsicHeight(progress);
                        thumb.setIntrinsicWidth(progress);
                        widthSeek.setThumb(thumb);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

                widthDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //stores brush width to restore to this position when Paint Width is selected again later
                        width = widthSeek.getProgress();
                        doodleView.setBrushWidth(width);
                        hideButtons();
                    }
                });

                widthDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                widthDialog.setView(widthSeek);
                widthDialog.show();
            }
        });

        //Paint color settings
        paintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder pickColorDialog = new AlertDialog.Builder(MainActivity.this);
                pickColorDialog.setTitle("Brush Color");
                pickColorDialog.setMessage("Select a brush color:");

                final SeekBar colorSeek = new SeekBar(MainActivity.this);
                colorSeek.setMax(1792);
                colorSeek.setKeyProgressIncrement(1);

                colorSeek.setProgress(colorProgress);
                colorSeek.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                colorSeek.getProgressDrawable().setAlpha(opacity);

                ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
                thumb.setIntrinsicHeight(width);
                thumb.setIntrinsicWidth(width);
                colorSeek.setThumb(thumb);

                colorSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekBar.getProgressDrawable().setColorFilter(progressColorInt(progress), PorterDuff.Mode.MULTIPLY);
                    }

                    //The following two methods are not used, only needed to complete abstract.
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

                pickColorDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        color = progressColorInt(colorSeek.getProgress());
                        doodleView.setColor(color);
                        doodleView.setPaintOpacity(opacity);
                        hideButtons(); //Call this so the artist can keep drawing on the full canvas!
                }
                });

                pickColorDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                pickColorDialog.setView(colorSeek);
                pickColorDialog.show();
            }
        });

        opacityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder opacityDialog = new android.app.AlertDialog.Builder(MainActivity.this);
                opacityDialog.setTitle("Brush Opacity").setMessage("Select your brush opacity:");

                final SeekBar opacitySeek = new SeekBar(MainActivity.this);
                opacitySeek.setMax(255);
                opacitySeek.setKeyProgressIncrement(1);
                opacitySeek.setMinimumHeight(100);/

                opacitySeek.setProgress(opacity);
                opacitySeek.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                opacitySeek.getProgressDrawable().setAlpha(opacity);

                ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
                thumb.setIntrinsicHeight(width);
                thumb.setIntrinsicWidth(width);
                opacitySeek.setThumb(thumb);

                opacitySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekBar.getProgressDrawable().setAlpha(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

                opacityDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        opacity = opacitySeek.getProgress();
                        doodleView.setPaintOpacity(opacity);
                        hideButtons();
                    }
                });

                opacityDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                opacityDialog.setView(opacitySeek);
                opacityDialog.show();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(MainActivity.this);
                saveDialog.setTitle("Save drawing to gallery");
                saveDialog.setMessage("Would you like to save your drawing to device Gallery?");

                saveDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doodleView.buildDrawingCache();
                        doodleView.setDrawingCacheEnabled(true);

                        String imgSaved = MediaStore.Images.Media.insertImage(
                                getContentResolver(), doodleView.getDrawingCache(),
                                UUID.randomUUID().toString()+".png", "drawing");
                        doodleView.destroyDrawingCache();

                        if(imgSaved!=null){
                            Toast savedToast = Toast.makeText(getApplicationContext(),
                                    "Your drawing has been saved to your gallery.", Toast.LENGTH_SHORT);
                            savedToast.show();
                        }
                        else{
                            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                    "Error, please check your permissions.", Toast.LENGTH_SHORT);
                            unsavedToast.show();
                        }
                        hideButtons();
                    }
                });
                saveDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Don't do anything
                    }
                });
                saveDialog.show();
            }
        });

    }

    public void hideButtons() {
        newButton.setVisibility(View.GONE);
        brushButton.setVisibility(View.GONE);
        paintButton.setVisibility(View.GONE);
        opacityButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        importButton.setVisibility(View.GONE);
        hidden = true;
    }

    public void showButtons() {
        newButton.setVisibility(View.VISIBLE);
        brushButton.setVisibility(View.VISIBLE);
        paintButton.setVisibility(View.VISIBLE);
        opacityButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        importButton.setVisibility(View.VISIBLE);
        hidden = false;
    }

    public int colorProgress(int progress) {
        return 0;
    }

    //Taken from stackoverflow, has all the colors settable into one line.
    private int progressColorInt(int progress) {
        int red = 0;
        int green = 0;
        int blue = 0;
        if(progress < 256) {
            blue = progress;
        } else if(progress < 256*2) {
            green = progress%256;
            blue = 256 - progress%256;
        } else if(progress < 256*3) {
            green = 255;
            blue = progress%256;
        } else if(progress < 256*4) {
            red = progress%256;
            green = 256 - progress%256;
            blue = 256 - progress%256;
        } else if(progress < 256*5) {
            red = 255;
            green = 0;
            blue = progress%256;
        } else if(progress < 256*6) {
            red = 255;
            green = progress%256;
            blue = 256 - progress%256;
        } else if(progress < 256*7) {
            red = 255;
            green = 255;
            blue = progress%256;
        }
        return(Color.argb(255, red, green, blue));
    }
}
