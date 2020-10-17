package com.letscoffee.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentWebViewBinding;

public class FragmentWebView extends BottomSheetDialogFragment {
    String title;
    String url;
    private FragmentWebViewBinding binding;
    private BottomSheetBehavior<View> behavior;

    public FragmentWebView setData(String title, String url) {
        this.title = title;
        this.url = url;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog=(BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        binding= DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_web_view,null,false);
        dialog.setContentView(binding.getRoot());
        behavior=BottomSheetBehavior.from((View)binding.getRoot().getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        BindView();
        return dialog;
    }

    private void BindView() {
        binding.mWebView.loadUrl(url);
        binding.tvTitle.setText(title);
        binding.mWebView.getSettings().setJavaScriptEnabled(true);
        binding.mWebView.setWebViewClient(new AppWebViewClients());
        binding.imgBack.setOnClickListener(v->dismiss());
    }

    @Override
    public void onResume() {
        super.onResume();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onStart() {
        super.onStart();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    public class AppWebViewClients extends WebViewClient {


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            binding.swipeRefresh.setRefreshing(true);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
          binding.swipeRefresh.setRefreshing(false);
        }
    }
}
