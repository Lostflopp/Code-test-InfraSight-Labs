package com.infrasight.kodtest;


import okhttp3.*;
import org.json.JSONObject;
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

	String apiURL = "http://localhost:" + TestVariables.API_PORT + "/api/";

	/**
	 * Simple example test which asserts that the Kodtest API is up and running.
	 */

	@Test
	public void connectionTest() throws InterruptedException {
		assertTrue(serverUp);
	}

	@Test
	public void assignment1() throws InterruptedException, IOException {
		// Create the URL object

		String URL = apiURL + "auth";


		OkHttpClient client = new OkHttpClient().newBuilder().build();

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, "{\r\n  \"user\": \"apiUser\",\r\n  \"password\": \"apiPassword!\"\r\n}");
		Request request = new Request.Builder()
				.url(URL)
				.method("POST", body)
				.addHeader("Content-Type", "application/json")
				.build();
		Response response = client.newCall(request).execute();
		long status = response.code();

		// Get the response body as a JSON object
		String responseBody = response.body().string();
		JSONObject jsonResponse  = new JSONObject(responseBody);

		// Extract the value
		String token = jsonResponse.getString("token");


		System.out.println("This is the token" + token);
		assertEquals(200, status);

		String param ="?filter=employeeId%3D1337";
		URL = apiURL + "accounts" + param;


		client = new OkHttpClient().newBuilder().build();

		request = new Request.Builder()
				.url(URL)
				.method("GET",null)
				.addHeader("Authorization", "Bearer "+token)
				.build();
		response = client.newCall(request).execute();
		status = response.code();

		System.out.println("This is the body" + response.body().string());
		assertEquals(200, status);

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
		assertEquals(3, groupCount);

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
