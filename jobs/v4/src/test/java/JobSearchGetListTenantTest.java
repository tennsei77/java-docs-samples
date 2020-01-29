/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static com.google.common.truth.Truth.assertThat;

import com.google.samples.JobSearchCreateTenant;
import com.google.samples.JobSearchDeleteTenant;
import com.google.samples.JobSearchGetTenant;
import com.google.samples.JobSearchListTenants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JobSearchGetListTenantTest {
  private static final String PROJECT_ID = System.getenv("GOOGLE_CLOUD_PROJECT");
  private static final String TENANT_EXT_ID = "EXTERNAL_TEMP_TENANT_ID";
  private String tenantId;

  private ByteArrayOutputStream bout;
  private PrintStream out;

  @Before
  public void setUp() throws IOException {
    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    System.setOut(out);
    // create a tenant for job and company
    JobSearchCreateTenant.createTenant(PROJECT_ID, TENANT_EXT_ID);

    String got = bout.toString();
    assertThat(got).contains("Created Tenant");

    tenantId = JobSearchListGetCompanyTest.extractLastId(got.split("\n")[1]);
  }

  @Test
  public void testGetListTenant() throws IOException {
    // retrieve tenant.
    JobSearchGetTenant.getTenant(PROJECT_ID, tenantId);
    String got = bout.toString();
    assertThat(got).contains(String.format("External ID: %s", TENANT_EXT_ID));
    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    System.setOut(out);

    // list tenants.
    JobSearchListTenants.listTenants(PROJECT_ID);
    got = bout.toString();
    assertThat(got).contains(TENANT_EXT_ID);
    assertThat(got).contains("Tenant Name:");
    assertThat(got).contains("External ID:");

    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    System.setOut(out);
  }

  @After
  public void tearDown() throws IOException {
    JobSearchDeleteTenant.deleteTenant(PROJECT_ID, tenantId);
    String got = bout.toString();
    assertThat(got).contains("Deleted Tenant.");

    System.setOut(null);
  }
}
