package hackstyle.org.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import hackstyle.org.wizard.Tela1;
import hackstyle.org.wizard.Tela2;
import hackstyle.org.wizard.Tela3;
import hackstyle.org.wizard.Tela4;
import hackstyle.org.wizard.Tela5;
import hackstyle.org.wizard.Tela6;


public class WizardAdapter extends FragmentPagerAdapter {

    public WizardAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                return new Tela1();

            case 1:
                return new Tela2();

            case 2:
                return new Tela3();

            case 3:
                return  new Tela4();

            case 4:
                return  new Tela5();

            case 5:
                return  new Tela6();

        }

        return null;
    }

    @Override
    public int getCount() {
        return 6;
    }
}
