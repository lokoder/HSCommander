package hackstyle.org.wizard;

import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.eftimoff.viewpagertransformers.CubeOutTransformer;

import hackstyle.org.adapter.WizardAdapter;
import hackstyle.org.hscommander.R;

public class WizardActivity extends FragmentActivity {

    WizardAdapter wizardAdapter;
    ViewPager pager;
    Button btnPrev, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        btnPrev = (Button)findViewById(R.id.wizard_previous_button);
        btnNext = (Button)findViewById(R.id.wizard_next_button);

        wizardAdapter = new WizardAdapter(getSupportFragmentManager());
        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(wizardAdapter);

        pager.setPageTransformer(true, new CubeOutTransformer());
        //pager.setPageTransformer(true, new FlipHorizontalTransformer());
        //pager.setPageTransformer(true, new ForegroundToBackgroundTransformer());
        //pager.setPageTransformer(true, new StackTransformer());
        //pager.setPageTransformer(true, new TabletTransformer());
        //pager.setPageTransformer(true, new ZoomInTransformer());
        //pager.setPageTransformer(true, new ZoomOutTranformer());

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                boolean state = (position < 1)? false : true;
                btnPrev.setEnabled(state);

                if (!state)
                    btnPrev.setAlpha(0.2F);
                else
                    btnPrev.setAlpha(1F);

                String label = (position+1 == wizardAdapter.getCount())? "Finalizar" : "Próximo";
                btnNext.setText(label);
            }
        });


        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int curPosition = pager.getCurrentItem();

                if (curPosition+1 > 1) {
                    pager.setCurrentItem(pager.getCurrentItem() - 1, true);
                }
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int numPages = wizardAdapter.getCount();
                int curPosition = pager.getCurrentItem();

                if (curPosition+1 < numPages) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);

                } else if (curPosition+1 == numPages) {
                    //MainActivity.this.finish();
                }
            }
        });


        btnPrev.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HighlandGothicFLF.ttf"));
        btnNext.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HighlandGothicFLF.ttf"));

    }

}