package net.abigailthompson.grocerylist;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroceryAdapter extends RecyclerView.Adapter {
    boolean isDeleting;
    private ArrayList<Grocery> GroceryData;
    private View.OnClickListener onItemClickListener;
    private CompoundButton.OnCheckedChangeListener onItemCheckedChangedListener;
    public static final String TAG = "GroceryAdapter";

    private Context parentContext;

    public void setDelete(boolean b) {
        isDeleting = b;
    }

    public class GroceryViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName;
        private TextView tvCity;
        private Button btnDelete;
        private CheckBox chkFavorite;

        private ImageButton imageButtonPhoto;

        private View.OnClickListener onClickListener;
        private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

        public GroceryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCity = itemView.findViewById(R.id.tvCity);
            chkFavorite = itemView.findViewById(R.id.chkFavorite);
            imageButtonPhoto = itemView.findViewById(R.id.imgPhoto);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
            chkFavorite.setTag(this);
            chkFavorite.setOnCheckedChangeListener(onCheckedChangeListener);
        }

        public TextView getName()
        {
            return tvName;
        }
        public TextView getCity()
        {
            return tvCity;
        }
        public Button getBtnDelete() {return btnDelete; }

        public CheckBox getChkFavorite()
        {
            return chkFavorite;
        }
        public ImageButton getImageButtonPhoto()
        {
            return imageButtonPhoto;
        }

    }

    public GroceryAdapter(ArrayList<Grocery> data, Context context)
    {
        GroceryData = data;
        Log.d(TAG, "GroceryAdapter: " + data.size());
        parentContext = context;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener)
    {
        Log.d(TAG, "setOnItemClickListener: ");
        onItemClickListener = itemClickListener;
    }

    public void setOnItemCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener)
    {
        Log.d(TAG, "setOnItemCheckedChangeListener: ");
        onItemCheckedChangedListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new GroceryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + GroceryData.get(position));

        Grocery grocery = GroceryData.get(position);

        GroceryViewHolder groceryViewHolder = (GroceryViewHolder) holder;
        groceryViewHolder.getName().setText(grocery.getName());
        groceryViewHolder.getCity().setText(grocery.getCity());
        //groceryViewHolder.getImageButtonPhoto().setImageResource(grocery.getImgId());
        groceryViewHolder.getChkFavorite().setChecked(grocery.getIsfavorite());

        Bitmap groceryPhoto = grocery.getPhoto();

        if(groceryPhoto != null)
        {
            groceryViewHolder.getImageButtonPhoto().setImageBitmap(groceryPhoto);
        }
        else {
            groceryViewHolder.getImageButtonPhoto().setImageResource(R.drawable.photoicon);
        }

        if(isDeleting)
            groceryViewHolder.getBtnDelete().setVisibility(View.VISIBLE);
        else
            groceryViewHolder.getBtnDelete().setVisibility(View.INVISIBLE);


        groceryViewHolder.getBtnDelete().setOnClickListener(view -> {
            Log.d(TAG, "onClick: Delete");
            deleteItem(position);
        });

        groceryViewHolder.getChkFavorite().setOnCheckedChangeListener((compoundButton, b) -> {
            Log.d(TAG, "onCheckedChanged: " + b);
            onItemCheckedChangedListener.onCheckedChanged(compoundButton, b);

            //grocery.setIsfavorite(b);
            //GroceryDataSource ds = new GroceryDataSource(parentContext);
            //ds.open();
            //boolean didUpdate = ds.update(grocery) > 0;
            /* RestClient.execPutRequest(grocery,
                    GroceryListActivity.TEAMSAPI + grocery.getId(),
                    this.parentContext,
                    VolleyCallback -> {
                        Log.d(TAG, "onSuccess: Put" + grocery.getId());
                    }); */
        });
    }

    private void deleteItem(int position) {
        try {
            Log.d(TAG, "deleteItem: Start");
            Grocery grocery = GroceryData.get(position);
            //GroceryDataSource ds = new GroceryDataSource(parentContext);
            //ds.open();
            Log.d(TAG, "deleteItem: " + grocery.getName());
            //boolean didDelete = ds.delete(grocery) > 0;
            RestClient.execDeleteRequest(grocery,
                    GroceryListActivity.TEAMSAPI + grocery.getId(),
                    this.parentContext,
                    VolleyCallback -> {
                        Log.d(TAG, "onSuccess: Delete" + grocery.getId());
                        GroceryData.remove(position);
                        notifyDataSetChanged();
                    });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return GroceryData.size();
    }
}

