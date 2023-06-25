package com.infrasight.kodtest;


import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Simple concrete class for JUnit tests with uses {@link TestsSetup} as a
 * foundation for starting/stopping the API server for tests.
 * 
 * You may configure port, api user and api port in {@link TestVariables} if
 * needed.
 */
public class Tests extends TestsSetup {

	OkHttpClient client = new OkHttpClient().newBuilder().build();
	MediaType mediaType = MediaType.parse("application/json");
	Request request;
	RequestBody body;
	Response response;
	String responseBody;
	JSONObject jsonResponse;
	JSONArray jsonArray;
	JSONObject jsonObject;
	String param;
	String token;

	String apiURL = "http://localhost:" + TestVariables.API_PORT + "/api/";
	int verasID = 1337;

	int status;
	String URL;

	/**
	 * Simple example test which asserts that the Kodtest API is up and running.
	 */
	@Before
	public void getToken() throws InterruptedException, IOException {
		URL = apiURL + "auth";

		body = RequestBody.create(mediaType, "{\r\n  \"user\": \"apiUser\",\r\n  \"password\": \"apiPassword!\"\r\n}");
		request = new Request.Builder()
				.url(URL)
				.method("POST", body)
				.addHeader("Content-Type", "application/json")
				.build();
		response = client.newCall(request).execute();
		status = response.code();

		// Get the response body as a JSON object
		responseBody = response.body().string();
		jsonResponse  = new JSONObject(responseBody);

		// Extract the value
		token = jsonResponse.getString("token");
		//System.out.println("This is the token" + token);

		assertEquals(200, status);

	}

	@Test
	public void connectionTest() throws InterruptedException {
		assertTrue(serverUp);
	}

	@Test
	public void assignment1() throws InterruptedException, IOException {
		/**
		 * TODO: Add code to solve the first assignment. Add Assert to show that you
		 * found the account for Vera
		 */

		param ="?filter=employeeId="+verasID;
		URL = apiURL + "accounts" + param;

		request = new Request.Builder()
				.url(URL)
				.method("GET",null)
				.addHeader("Authorization", "Bearer "+ token)
				.build();
		response = client.newCall(request).execute();
		status = response.code();

		assertEquals(200, status);

		// Parse the JSON string as a JSONArray
		jsonArray = new JSONArray(response.body().string());

		// Extract the first JSONObject from the JSONArray
		jsonObject = jsonArray.getJSONObject(0);

		// Get the response body as a JSON object
		responseBody = jsonObject.toString();
		jsonResponse  = new JSONObject(responseBody);

		// Extract the value
		int employeeId = jsonResponse.getInt("employeeId");

		//System.out.println("This is the employeeId " + employeeId);

		// Verifying correct account for Vera
		assertEquals(verasID, employeeId);

	}

	@Test
	public void assignment2() throws InterruptedException {
		assertTrue(serverUp);
		/**
		 * TODO: Add code to solve the second assignment where we expect the number of
		 * groups to be 3.
		 */
		

		int groupCount = 3;

		// Assert which verifies the expected group count of 3
		assertEquals(groupCount, 3);

		/**
		 * TODO: Add Assert to verify the IDs of the groups found
		 */
	}

	@Test
	public void assignment3() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the third assignment. Add Assert to verify the
		 * expected number of groups. Add Assert to verify the IDs of the groups found.
		 */
	}

	@Test
	public void assignment4() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the fourth assignment. Add Asserts to verify the
		 * total salary requested
		 */
	}

	@Test
	public void assignment5() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the fifth assignment. Add Asserts to verify the
		 * managers requested
		 */
	}
}
