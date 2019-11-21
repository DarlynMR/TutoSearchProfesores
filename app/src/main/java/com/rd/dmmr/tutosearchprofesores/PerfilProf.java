package com.rd.dmmr.tutosearchprofesores;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

public class PerfilProf extends AppCompatActivity {


    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_prof);

        setUpView();
        setUpViewPagerAdapter();


    }


    private void setUpView() {

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        appBarLayout = (AppBarLayout) findViewById(R.id.MyAppbar);
        viewPager = (ViewPager) findViewById(R.id.viewPagerP);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
    }

    private void setUpViewPagerAdapter() {
        viewPagerAdapter.addFragment(new fragment_DatosPerfil(), "DATOS");
        viewPagerAdapter.addFragment(new fragment_Valoraciones(), "VALORACIONES");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

}
