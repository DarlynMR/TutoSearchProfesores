package com.rd.dmmr.tutosearchprofesores.notificaciones;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAARR8a-gA:APA91bGdFa_aIHcfnwRzYRa50whVO_NjuWwS6lxLxs4YQTkBbG3jj3ma9XBW5d4LOYXuI2RC-3poxX9L910eO9Z_dD2p2G7ugo6DIhGNPXVbnRG36YDZ5pbkdyxJt4s0X72BpvrIwdGn"
            }
    )

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
