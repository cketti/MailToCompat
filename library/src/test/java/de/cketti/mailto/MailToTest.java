/*
 * Copyright (C) 2020 cketti
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cketti.mailto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class MailToTest {
    private static final String MAILTOURI_1 = "mailto:chris@example.com";
    private static final String MAILTOURI_2 = "mailto:infobot@example.com?subject=current-issue";
    private static final String MAILTOURI_3 =
            "mailto:infobot@example.com?body=send%20current-issue";
    private static final String MAILTOURI_4 = "mailto:infobot@example.com?body=send%20current-" +
            "issue%0D%0Asend%20index";
    private static final String MAILTOURI_5 = "mailto:joe@example.com?" +
            "cc=bob@example.com&body=hello";
    private static final String MAILTOURI_6 = "mailto:?to=joe@example.com&" +
            "cc=bob@example.com&body=hello";

    @SuppressWarnings("ConstantConditions")
    @Test
    public void isMailTo_withNullArgument_shouldReturnFalse() {
        assertFalse(MailTo.isMailTo(null));
    }

    @Test
    public void isMailTo_withEmptyString_shouldReturnFalse() {
        assertFalse(MailTo.isMailTo(""));
    }

    @Test
    public void isMailTo_withHttpUrl_shouldReturnFalse() {
        assertFalse(MailTo.isMailTo("http://www.google.com"));
    }

    @Test
    public void isMailTo_withValidMailtoUris_shouldReturnTrue() {
        assertTrue(MailTo.isMailTo(MAILTOURI_1));
        assertTrue(MailTo.isMailTo(MAILTOURI_2));
        assertTrue(MailTo.isMailTo(MAILTOURI_3));
        assertTrue(MailTo.isMailTo(MAILTOURI_4));
        assertTrue(MailTo.isMailTo(MAILTOURI_5));
        assertTrue(MailTo.isMailTo(MAILTOURI_6));
    }

    @Test
    public void simpleMailtoUri() {
        MailTo mailTo = MailTo.parse(MAILTOURI_1);

        assertEquals("chris@example.com", mailTo.getTo());
        assertEquals(1, mailTo.getHeaders().size());
        assertNull(mailTo.getBody());
        assertNull(mailTo.getCc());
        assertNull(mailTo.getSubject());
        assertEquals("mailto:?to=chris%40example.com&", mailTo.toString());
    }

    @Test
    public void subjectQueryParameter() {
        MailTo mailTo = MailTo.parse(MAILTOURI_2);

        assertEquals(2, mailTo.getHeaders().size());
        assertEquals("infobot@example.com", mailTo.getTo());
        assertEquals("current-issue", mailTo.getSubject());
        assertNull(mailTo.getBody());
        assertNull(mailTo.getCc());

        String stringUrl = mailTo.toString();

        assertTrue(stringUrl.startsWith("mailto:?"));
        assertTrue(stringUrl.contains("to=infobot%40example.com&"));
        assertTrue(stringUrl.contains("subject=current-issue&"));
    }

    @Test
    public void bodyQueryParameter() {
        MailTo mailTo = MailTo.parse(MAILTOURI_3);

        assertEquals(2, mailTo.getHeaders().size());
        assertEquals("infobot@example.com", mailTo.getTo());
        assertEquals("send current-issue", mailTo.getBody());
        assertNull(mailTo.getCc());
        assertNull(mailTo.getSubject());

        String stringUrl = mailTo.toString();

        assertTrue(stringUrl.startsWith("mailto:?"));
        assertTrue(stringUrl.contains("to=infobot%40example.com&"));
        assertTrue(stringUrl.contains("body=send%20current-issue&"));
    }

    @Test
    public void bodyQueryParameterWithLineBreak() {
        MailTo mailTo = MailTo.parse(MAILTOURI_4);

        assertEquals(2, mailTo.getHeaders().size());
        assertEquals("infobot@example.com", mailTo.getTo());
        assertEquals("send current-issue\r\nsend index", mailTo.getBody());
        assertNull(mailTo.getCc());
        assertNull(mailTo.getSubject());

        String stringUrl = mailTo.toString();

        assertTrue(stringUrl.startsWith("mailto:?"));
        assertTrue(stringUrl.contains("to=infobot%40example.com&"));
        assertTrue(stringUrl.contains("body=send%20current-issue%0D%0Asend%20index&"));
    }

    @Test
    public void ccAndBodyQueryParameters() {
        MailTo mailTo = MailTo.parse(MAILTOURI_5);

        assertEquals(3, mailTo.getHeaders().size());
        assertEquals("joe@example.com", mailTo.getTo());
        assertEquals("bob@example.com", mailTo.getCc());
        assertEquals("hello", mailTo.getBody());
        assertNull(mailTo.getSubject());

        String stringUrl = mailTo.toString();

        assertTrue(stringUrl.startsWith("mailto:?"));
        assertTrue(stringUrl.contains("cc=bob%40example.com&"));
        assertTrue(stringUrl.contains("body=hello&"));
        assertTrue(stringUrl.contains("to=joe%40example.com&"));
    }

    @Test
    public void toAndCcQueryParameters() {
        MailTo mailTo = MailTo.parse(MAILTOURI_6);

        assertEquals(3, mailTo.getHeaders().size());
        assertEquals(", joe@example.com", mailTo.getTo());
        assertEquals("bob@example.com", mailTo.getCc());
        assertEquals("hello", mailTo.getBody());
        assertNull(mailTo.getSubject());

        String stringUrl = mailTo.toString();

        assertTrue(stringUrl.startsWith("mailto:?"));
        assertTrue(stringUrl.contains("cc=bob%40example.com&"));
        assertTrue(stringUrl.contains("body=hello&"));
        assertTrue(stringUrl.contains("to=%2C%20joe%40example.com&"));
    }

    @Test
    public void encodedAmpersandInBody() {
        MailTo mailTo = MailTo.parse("mailto:alice@example.com?body=a%26b");

        assertEquals("a&b", mailTo.getBody());
    }

    @Test
    public void encodedEqualSignInBody() {
        MailTo mailTo = MailTo.parse("mailto:alice@example.com?body=a%3Db");

        assertEquals("a=b", mailTo.getBody());
    }

    @Test
    public void unencodedEqualsSignInBody() {
        // This is not a properly encoded mailto URI. But there's no good reason to drop everything
        // after the equals sign in the 'body' query parameter value.
        MailTo mailTo = MailTo.parse("mailto:alice@example.com?body=foo=bar&subject=test");

        assertEquals("foo=bar", mailTo.getBody());
        assertEquals("test", mailTo.getSubject());
    }

    @Test
    public void encodedPercentValueInBody() {
        MailTo mailTo = MailTo.parse("mailto:alice@example.com?body=%2525");

        assertEquals("%25", mailTo.getBody());
    }

    @Test
    public void colonInBody() {
        MailTo mailTo = MailTo.parse("mailto:alice@example.com?body=one:two");

        assertEquals("one:two", mailTo.getBody());
    }

    @Test
    public void emailAddressAndFragment() {
        MailTo mailTo = MailTo.parse("mailto:alice@example.com#fragment");

        assertEquals("alice@example.com", mailTo.getTo());
    }

    @Test
    public void emailAddressAndQueryAndFragment() {
        MailTo mailTo = MailTo.parse("mailto:alice@example.com?cc=bob@example.com#fragment");

        assertEquals("alice@example.com", mailTo.getTo());
        assertEquals("bob@example.com", mailTo.getCc());
    }

    @Test
    public void fragmentWithValueThatLooksLikeQueryPart() {
        MailTo mailTo = MailTo.parse("mailto:#?to=alice@example.com");

        assertEquals("", mailTo.getTo());
    }
}
