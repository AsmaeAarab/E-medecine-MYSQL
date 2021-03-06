package com.example.e_medecine.ApiRest;

import com.example.e_medecine.Docteurs.Medecin;

import com.example.e_medecine.Docteurs.Rendezvous;
import com.example.e_medecine.model.RDV;
import com.example.e_medecine.model.Users;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MedecinService {
    /*@GET("listerPatient")
    Call<List<Users>> getPatients();*/
    //@Headers("Content-Type: application/json")
    // GetRequest.SetContentEncoding("json")


    @GET("listerPatient")
    Call<List<Users>> getUsers();
    @Headers({"Accept: application/json"})
    @POST("find/insert/u/user")
    Call<Users>addUserM(@Body Users user);
    @Headers({"Accept: application/json"})
    @POST("find/insert/m/medecin")
    Call<Medecin>addMedecin(@Body Medecin docteur);

    @GET("find/user/Phone/login/{Medecin}/{Password}/{Phone}")
    Call<Users>FinduserbyPhone(@Path("Password") String Pass, @Path("Phone") String Phone, @Path("Medecin") String Docteur);

    @GET("find/user/{Phone}")
    Call<List<Users>>getIdUser(@Path("Phone") String Phone);

    @GET("allDoc/{id_specialite}/{type_medecin}")
    Call<List<com.example.e_medecine.model.Medecin>> getMedecinList(@Path("id_specialite")int idSpecialite, @Path("type_medecin")String typeMedecin);
    @GET("find/user/Email/login/{Email}/{Password}/{Docteur}")
    Call<List<Users>>isEmailValid(@Path("Email") String Email,@Path("Password") String Password,@Path("Docteur") String Docteur);
    @GET("find/user/Acceuil/{log}/")
    Call<List<Users>>GetElementUsers(@Path("log") String log);
    @POST("find/update/{Mail}/m")
    Call<Users>UpdateMedecin(@Body Users users,@Path("Mail") String Mail);
    @POST("find/update/Nom/{ID}/m")
    Call<Users>UpdateMedecinNom(@Body Users users,@Path("ID") int ID);
    @POST("find/update/Prenom/{ID}/m")
    Call<Users>UpdateMedecinPrenom(@Body Users users,@Path("ID") int ID);
    @POST("find/update/Phone/{ID}/m")
    Call<Users>UpdateMedecinPhone(@Body Users users,@Path("ID") int ID);
    @POST("find/update/Email/{ID}/m")
    Call<Users>UpdateMedecinEmail(@Body Users users,@Path("ID") int ID);
    @POST("find/update/Image/{ID}/m")
    Call<Users>UpdateMedecinImage(@Body Users users,@Path("ID") int ID);
    @POST("find/update/Date/{IDR}/m")
    Call<RDV>UpdateMedecinCalendar(@Body RDV rdv, @Path("IDR") int IDR);
    @GET("find/medecin/intellij/{ID}/")
    Call<List<Medecin>>GetIdMedecin(@Path("ID") int ID);

    @GET("find/rendezvous/intellij/{ID}/")
    Call<List<Rendezvous>>GetPtaientsData(@Path("ID") int ID);

    @GET("find/rendezvous/{ID}/")
    Call<List<Rendezvous>>GetMedecinData(@Path("ID") int ID);

}
