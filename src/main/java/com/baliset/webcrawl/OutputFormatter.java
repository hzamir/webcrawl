package com.baliset.webcrawl;

import com.baliset.webcrawl.model.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.xml.*;
import com.fasterxml.jackson.dataformat.yaml.*;

import java.io.*;

public class OutputFormatter
{
  private ObjectMapper objectMapper;


  public void selectFormat(String fmt)
  {
      switch(fmt) {
        case  "xml": objectMapper = new XmlMapper(); break;
        case "json": objectMapper = new ObjectMapper(); break;
        case "yaml": objectMapper = new YAMLMapper(); break;
        default:     objectMapper = new ObjectMapper();
      }

      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

  }



  public void print(CrawlResults crawlResults, Writer writer)
  {
    try {
      objectMapper.writeValue(writer, crawlResults);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
