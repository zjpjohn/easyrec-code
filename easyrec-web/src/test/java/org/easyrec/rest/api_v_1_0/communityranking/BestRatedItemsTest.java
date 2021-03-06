/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
 *
 * This file is part of easyrec.
 *
 * easyrec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * easyrec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.easyrec.rest.api_v_1_0.communityranking;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.test.framework.spi.container.TestContainerException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.easyrec.rest.api_v_1_0.AbstractApiTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;

import javax.ws.rs.core.MultivaluedMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * ${DESCRIPTION}
 * <p/>
 * <p><b>Company:</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:</b>
 * (c) 2011</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: $<br/>
 * $Date: $<br/>
 * $Revision: $</p>
 *
 * @author patrick
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@DataSet(AbstractApiTest.DATASET_BASE)
public abstract class BestRatedItemsTest extends AbstractApiTest {

    public static class JSON extends BestRatedItemsTest {
        public JSON() throws TestContainerException { super(METHOD_JSON); }
    }

    public static class XML extends BestRatedItemsTest {
        public XML() throws TestContainerException { super(METHOD_XML); }
    }

    public BestRatedItemsTest(String method) throws TestContainerException {
        super("bestrateditems", method);
    }

    @Test
    public void bestRatedItems_noParameters_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("299"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Wrong APIKey/Tenant combination"));
    }

    @Test
    public void bestRatedItems_wrongTenant_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", "WRONG-TENANT");
        params.add("apikey", API_KEY);

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("299"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Wrong APIKey/Tenant combination"));
    }

    @Test
    public void bestRatedItems_wrongApiKey_shouldReturnError() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", "0123456789abcdefedcba98765432100");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getJSONObject("error").getString("@code"), is("299"));
        assertThat(json.getJSONObject("error").getString("@message"),
                containsString("Wrong APIKey/Tenant combination"));
    }

    @Test
    public void bestRatedItems_apiKeyTenant_shouldReturnRecommendations() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getString("tenantid"), is(TENANT_ID));
        assertThat(json.getString("action"), is("bestrateditems"));

        JSONArray items = getSafeJSONArray(json, "recommendeditems", "item");

        assertThat(items.size(), is(2));

        JSONObject item = items.getJSONObject(0);
        assertThat(item.getString("creationDate"), is("2011-06-22 12:01:00.0"));
        assertThat(item.getString("description"), is("Item A"));
        assertThat(item.getString("imageUrl"), is("http://testtenant.com/test/item/a/img.png"));
        assertThat(item.getString("id"), is("ITEM_A"));
        assertThat(item.getString("itemType"), is("ITEM"));
        assertThat(item.getString("url"), is("http://testtenant.com/test/item/a"));
        assertThat(item.getString("value"), is("9.0"));

        item = items.getJSONObject(1);
        assertThat(item.getString("creationDate"), is("2011-06-22 12:04:00.0"));
        assertThat(item.getString("description"), is("Other A"));
        assertThat(item.getString("imageUrl"), is("http://testtenant.com/test/other/a/img.png"));
        assertThat(item.getString("id"), is("OTHER_A"));
        assertThat(item.getString("itemType"), is("OTHER_ITEM"));
        assertThat(item.getString("url"), is("http://testtenant.com/test/other/a"));
        assertThat(item.getString("value"), is("9.0"));
    }

    @Test
    public void bestRatedItems_numberOfResults_shouldTruncateResult() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("numberOfResults", "1");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getString("tenantid"), is(TENANT_ID));
        assertThat(json.getString("action"), is("bestrateditems"));

        JSONArray items = getSafeJSONArray(json, "recommendeditems", "item");

        assertThat(items.size(), is(1));

        JSONObject item = items.getJSONObject(0);
        assertThat(item.getString("creationDate"), is("2011-06-22 12:01:00.0"));
        assertThat(item.getString("description"), is("Item A"));
        assertThat(item.getString("imageUrl"), is("http://testtenant.com/test/item/a/img.png"));
        assertThat(item.getString("id"), is("ITEM_A"));
        assertThat(item.getString("itemType"), is("ITEM"));
        assertThat(item.getString("url"), is("http://testtenant.com/test/item/a"));
        assertThat(item.getString("value"), is("9.0"));
    }

    @Test
    public void bestRatedItems_requestedItemType_shouldFilterResults() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("tenantid", TENANT_ID);
        params.add("apikey", API_KEY);
        params.add("requesteditemtype", "OTHER_ITEM");

        JSONObject json = makeAPIRequest(params);

        assertThat(json, not(is(nullValue())));
        assertThat(json.getString("tenantid"), is(TENANT_ID));
        assertThat(json.getString("action"), is("bestrateditems"));

        JSONArray items = getSafeJSONArray(json, "recommendeditems", "item");

        assertThat(items.size(), is(1));

        JSONObject item = items.getJSONObject(0);
        assertThat(item.getString("creationDate"), is("2011-06-22 12:04:00.0"));
        assertThat(item.getString("description"), is("Other A"));
        assertThat(item.getString("imageUrl"), is("http://testtenant.com/test/other/a/img.png"));
        assertThat(item.getString("id"), is("OTHER_A"));
        assertThat(item.getString("itemType"), is("OTHER_ITEM"));
        assertThat(item.getString("url"), is("http://testtenant.com/test/other/a"));
        assertThat(item.getString("value"), is("9.0"));
    }
}
