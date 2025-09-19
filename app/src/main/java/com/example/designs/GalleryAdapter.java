package com.example.designs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private final Context context;
    private final List<GalleryItem> galleryItemList;
    private final String userEmail;

    public GalleryAdapter(Context context, List<GalleryItem> galleryItemList, String userEmail) {
        this.context = context;
        this.galleryItemList = galleryItemList;
        this.userEmail = userEmail;
    }

    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false);
        return new ViewHolder(view, new ViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position != RecyclerView.NO_POSITION) {
                    GalleryItem selectedItem = galleryItemList.get(position);

                    Intent intent = new Intent(context, MeasurementsPage.class);
                    intent.putExtra("selectedImageRes", selectedItem.getImageResId());
                    intent.putExtra("user_email", userEmail);


                    intent.putExtra(OrdersPage.EXTRA_DESIGN_NAME, selectedItem.getName());
                    intent.putExtra(OrdersPage.EXTRA_UNIT_PRICE, selectedItem.getPrice());
                    intent.putExtra(OrdersPage.EXTRA_DESIGN_ID, selectedItem.getImageResId());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.ViewHolder holder, int position) {
        GalleryItem item = galleryItemList.get(position);

        holder.imageView.setImageResource(item.getImageResId());
        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(String.format(Locale.getDefault(), "Ksh %.2f", item.getPrice()));
    }

    @Override
    public int getItemCount() {
        return galleryItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView priceTextView;
        OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            imageView = itemView.findViewById(R.id.galleryImage);
            nameTextView = itemView.findViewById(R.id.galleryItemName);
            priceTextView = itemView.findViewById(R.id.galleryItemPrice);

            imageView.setOnClickListener(v -> {
                listener.onItemClick(getAdapterPosition());
            });
        }
    }
}