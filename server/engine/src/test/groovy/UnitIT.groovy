import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static org.junit.Assert.*

/**
 * Tests for the Unit API.
 */
class UnitIT extends BaseApiTest {

    def unitUids = [
            '1BB3DAA7A390',
            '2BB3DAA7A390',
            '3BB3DAA7A390'];

    def unitNames = [
            'Test Unit One',
            'Test Unit Two',
            'Test Unit Three'];

    def unitInternalSymbols = [
            'kg',
            'kWh',
            'm'];

    def unitExternalSymbols = [
            'zkg',
            'zkWh',
            'zm'];

    /**
     * Tests for creation, fetch and deletion of a Unit using JSON responses.
     *
     * Create a new Unit by POSTing to '/units/types/{UID|name}/units'
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>name
     * <li>internalSymbol
     * <li>externalSymbol
     * </ul>
     *
     * See getAllUnitsForUnitTypeJson below for supported GET matrix parameters.
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Delete (TRASH) a Unit by sending a DELETE request to '/units/types/{UID|name}/units/{UID/symbol}'.
     *
     */
    @Test
    void createAndRemoveUnitJson() {
        versions.each { version -> createAndRemoveUnitJson(version) }
    }

    def createAndRemoveUnitJson(version) {
        if (version >= 3.5) {
            createAndRemoveUnitJson(version, 'Ounce', 'oz', 'ounce');
            createAndRemoveUnitJson(version, 'Angstrom', javax.measure.unit.NonSI.ANGSTROM.toString(), 'ang');
            createAndRemoveUnitJson(version, 'Meters Per Second', 'm/s', 'm/s');
        }
    }

    def createAndRemoveUnitJson(version, name, internalSymbol, externalSymbol) {

        setAdminUser();

        // Create a new Unit.
        def responsePost = client.post(
                path: "/${version}/units/types/1AA3DAA7A390/units",
                body: [
                        name: name,
                        internalSymbol: internalSymbol,
                        externalSymbol: externalSymbol],
                requestContentType: URLENC,
                contentType: JSON);
        assertEquals 201, responsePost.status;

        // Get and check the location.
        def unitLocation = responsePost.headers['Location'].value;
        def unitUid = unitLocation.split('/')[8];
        assertTrue unitUid.size() == 12;

        // Fetch the Unit.
        def response = client.get(
                path: "/${version}/units/types/1AA3DAA7A390/units/${unitUid};full",
                contentType: JSON);
        assertEquals 200, response.status;
        assertEquals 'application/json', response.contentType;
        assertTrue response.data instanceof net.sf.json.JSON;
        assertEquals 'OK', response.data.status;
        assertEquals name, response.data.unit.name;
        assertEquals internalSymbol, response.data.unit.internalSymbol;
        assertEquals externalSymbol, response.data.unit.externalSymbol;

        // Then delete the Unit.
        def responseDelete = client.delete(path: "/${version}/units/types/1AA3DAA7A390/units/${unitUid}");
        assertEquals 200, responseDelete.status;

        // We should get a 404 here.
        try {
            client.get(path: "/${version}/units/types/1AA3DAA7A390/units/${unitUid}");
            fail 'Should have thrown an exception';
        } catch (HttpResponseException e) {
            assertEquals 404, e.response.status;
        }
    }

    /**
     * Tests fetching a single unit.
     *
     * Unit GET requests support the following matrix parameters to modify the response.
     *
     * <ul>
     * <li>full - include all values.
     * <li>audit - include the status, created and modified values.
     * <li>symbols - include the internalSymbol and externalSymbol values.
     * <li>unitType - include the unitType value. This is the UID and name of the Unit Type.
     * <li>internalUnit - include the JScience toString value based in the internalSymbol.
     * <li>alternatives - include a list of alternative units (those that share the unit type).
     * </ul>
     *
     * By default the unit UID, name and implicit symbol are included.
     */
    @Test
    void getSingleUnitJson() {
        versions.each { version -> getSingleUnitJson(version) }
    }

    def getSingleUnitJson(version) {
        if (version >= 3.5) {
            def response = client.get(
                    path: "/${version}/units/types/AAA3DAA7A390/units/kg;full",
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertEquals '1BB3DAA7A390', response.data.unit.uid;
            assertEquals 'Test Unit One', response.data.unit.name;
            assertEquals 'zkg', response.data.unit.symbol;
            assertEquals 'kg', response.data.unit.internalSymbol;
            assertEquals 'zkg', response.data.unit.externalSymbol;
            assertEquals 2, response.data.alternatives.size();
            assertEquals(['2BB3DAA7A390', '3BB3DAA7A390'].sort(), response.data.alternatives.collect {it.uid}.sort());
            assertEquals(['Test Unit Two', 'Test Unit Three'].sort(), response.data.alternatives.collect {it.name}.sort());
            assertEquals(['zkWh', 'zm'].sort(), response.data.alternatives.collect {it.symbol}.sort());
        }
    }

    /**
     * Tests fetching a list of Units for a Unit Type using JSON.
     *
     * Units GET requests support the same matrix parameters as GETs for a single unit, except for the
     * alternatives matrix parameter.
     *
     * By default the unit UID, name and implicit symbol are included.
     *
     * Units are sorted by symbol.
     */
    @Test
    void getAllUnitsForUnitTypeJson() {
        versions.each { version -> getAllUnitsForUnitTypeJson(version) }
    }

    def getAllUnitsForUnitTypeJson(version) {
        if (version >= 3.5) {
            def response = client.get(
                    path: "/${version}/units/types/AAA3DAA7A390/units;full",
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertEquals unitUids.size(), response.data.units.size();
            assertEquals unitUids.sort(), response.data.units.collect {it.uid}.sort();
            assertEquals unitNames.sort(), response.data.units.collect {it.name}.sort();
            assertEquals unitInternalSymbols.sort(), response.data.units.collect {it.internalSymbol}.sort();
            assertEquals unitExternalSymbols.sort(), response.data.units.collect {it.externalSymbol}.sort();
        }
    }

    /**
     * Tests the validation rules for the Unit name field.
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>Mandatory.
     * <li>Unique on lower case of entire string amongst all Units.
     * <li>No longer than 255 characters.
     * </ul>
     */
    @Test
    void updateWithInvalidName() {
        setAdminUser();
        updateUnitFieldJson('name', 'empty', '');
        updateUnitFieldJson('name', 'long', String.randomString(256));
    }

    /**
     * Tests the validation rules for the Unit internalSymbol field.
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>Mandatory.
     * <li>Unique on lower case of entire string amongst all symbols in all Units.
     * <li>No longer than 255 characters.
     * <li>Must be a valid unit symbol recognised by JScience.
     * </ul>
     */
    @Test
    void updateWithInvalidInternalSymbol() {
        setAdminUser();
        updateUnitFieldJson('internalSymbol', 'empty', '');
        updateUnitFieldJson('internalSymbol', 'long', String.randomString(256));
        updateUnitFieldJson('internalSymbol', 'duplicate', 'kWh'); // Existing internalSymbol.
        updateUnitFieldJson('internalSymbol', 'format', 'not_a_real_unit_symbol');
    }

    /**
     * Tests the validation rules for the Unit externalSymbol field.
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>Optional.
     * <li>Unique on lower case of entire string amongst all symbols in all Units.
     * <li>No longer than 255 characters.
     * </ul>
     */
    @Test
    void updateWithInvalidExternalSymbol() {
        setAdminUser();
        updateUnitFieldJson('externalSymbol', 'long', String.randomString(256));
    }

    /**
     * Submits a single Unit field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     */
    def updateUnitFieldJson(field, code, value) {
        updateInvalidFieldJson("/units/types/AAA3DAA7A390/units/1BB3DAA7A390", field, code, value, 3.5)
    }
}