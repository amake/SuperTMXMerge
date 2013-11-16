/*
 * Copyright (C) 2013 Aaron Madlon-Kay <aaron@madlon-kay.com>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package org.madlonkay.supertmxmerge.data.JAXB;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * This class pretty-prints a TMX such that it is indented, but does not
 * add extra whitespace to &lt;seg> elements, as whitespace therein is
 * meaningful.
 * 
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class TmxWriter implements XMLStreamWriter {
    
    private final XMLStreamWriter writer;
    
    private int level = 0;
    private boolean noIndent;
    private boolean isComplexContent;
    
    private final Deque<String> tags = new ArrayDeque<String>();
    
    public TmxWriter(XMLStreamWriter writer) {
        this.writer = writer;
    }

    @Override
    public void writeStartElement(String string) throws XMLStreamException {
        startTag(string);
        writer.writeStartElement(string);
    }

    @Override
    public void writeStartElement(String string, String string1) throws XMLStreamException {
        startTag(string1);
        writer.writeStartElement(string, string1);
    }

    @Override
    public void writeStartElement(String string, String string1, String string2) throws XMLStreamException {
        startTag(string1);
        writer.writeStartElement(string, string1, string2);
    }

    @Override
    public void writeEmptyElement(String string, String string1) throws XMLStreamException {
        indent();
        writer.writeEmptyElement(string, string1);
    }

    @Override
    public void writeEmptyElement(String string, String string1, String string2) throws XMLStreamException {
        indent();
        writer.writeEmptyElement(string, string1, string2);
    }

    @Override
    public void writeEmptyElement(String string) throws XMLStreamException {
        indent();
        writer.writeEmptyElement(string);
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        endTag();
        writer.writeEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        writer.writeEndDocument();
        level = 0;
    }

    @Override
    public void close() throws XMLStreamException {
        writer.close();
    }

    @Override
    public void flush() throws XMLStreamException {
        writer.flush();
    }

    @Override
    public void writeAttribute(String string, String string1) throws XMLStreamException {
        writer.writeAttribute(string, string1);
    }

    @Override
    public void writeAttribute(String string, String string1, String string2, String string3) throws XMLStreamException {
        writer.writeAttribute(string, string1, string2, string3);
    }

    @Override
    public void writeAttribute(String string, String string1, String string2) throws XMLStreamException {
        writer.writeAttribute(string, string1, string2);
    }

    @Override
    public void writeNamespace(String string, String string1) throws XMLStreamException {
        writer.writeNamespace(string, string1);
    }

    @Override
    public void writeDefaultNamespace(String string) throws XMLStreamException {
        writer.writeDefaultNamespace(string);
    }

    @Override
    public void writeComment(String string) throws XMLStreamException {
        writer.writeComment(string);
    }

    @Override
    public void writeProcessingInstruction(String string) throws XMLStreamException {
        writer.writeProcessingInstruction(string);
    }

    @Override
    public void writeProcessingInstruction(String string, String string1) throws XMLStreamException {
        writer.writeProcessingInstruction(string, string1);
    }

    @Override
    public void writeCData(String string) throws XMLStreamException {
        writer.writeCData(string);
    }

    @Override
    public void writeDTD(String string) throws XMLStreamException {
        writer.writeDTD(string);
    }

    @Override
    public void writeEntityRef(String string) throws XMLStreamException {
        writer.writeEntityRef(string);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        writer.writeStartDocument();
        level = 0;
    }

    @Override
    public void writeStartDocument(String string) throws XMLStreamException {
        writer.writeStartDocument(string);
        level = 0;
    }

    @Override
    public void writeStartDocument(String string, String string1) throws XMLStreamException {
        writer.writeStartDocument(string, string1);
        level = 0;
    }

    @Override
    public void writeCharacters(String string) throws XMLStreamException {
        writer.writeCharacters(string);
    }

    @Override
    public void writeCharacters(char[] chars, int i, int i1) throws XMLStreamException {
        writer.writeCharacters(chars, i, i1);
    }

    @Override
    public String getPrefix(String string) throws XMLStreamException {
        return writer.getPrefix(string);
    }

    @Override
    public void setPrefix(String string, String string1) throws XMLStreamException {
        writer.setPrefix(string, string1);
    }

    @Override
    public void setDefaultNamespace(String string) throws XMLStreamException {
        writer.setDefaultNamespace(string);
    }

    @Override
    public void setNamespaceContext(NamespaceContext nc) throws XMLStreamException {
        writer.setNamespaceContext(nc);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return writer.getNamespaceContext();
    }

    @Override
    public Object getProperty(String string) throws IllegalArgumentException {
        return writer.getProperty(string);
    }
    
    private void startTag(String tag) throws XMLStreamException {
        indent();
        if ("seg".equals(tag)) {
            noIndent = true;
        }
        tags.push(tag);
        level++;
        isComplexContent = false;
    }
    
    private void endTag() throws XMLStreamException {
        level--;
        if (isComplexContent) {
            indent();
        }
        String tag = tags.pop();
        if ("seg".equals(tag)) {
            noIndent = false;
        }
        isComplexContent = true;
    }
    
    private void indent() throws XMLStreamException {
        if (noIndent) {
            return;
        }
        writer.writeCharacters("\n");
        for (int i = 0; i < level; i++) {
            writer.writeCharacters("    ");
        }
    }
    
}
