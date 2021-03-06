package edu.pdx.cs410J.rag4;

import com.google.common.annotations.VisibleForTesting;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * This servlet ultimately provides a REST API for working with an
 * <code>Airline</code>.  However, in its current state, it is an example
 * of how to use HTTP and Java servlets to store simple dictionary of words
 * and their definitions.
 */
public class AirlineServlet extends HttpServlet {
  static final String AIRLINE_NAME_PARAMETER = "airline";
  static final String FLIGHT_NUMBER_PARAMETER = "flightNumber";
  static final String SOURCE_PARAMETER = "src";
  static final String DEPARTURE_PARAMETER = "depart";
  static final String DESTINATION_PARAMETER = "dest";
  static final String ARRIVAL_PARAMETER = "arrive";

  private final Map<String, Airline> airlines = new HashMap<>();

  /**
   * Handles an HTTP GET request from a client by writing the definition of the
   * word specified in the "word" HTTP parameter to the HTTP response.  If the
   * "word" parameter is not specified, all of the entries in the dictionary
   * are written to the HTTP response.
   */
  @Override
  protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
  {
      response.setContentType( "text/plain" );

      String airlineName = getParameter(AIRLINE_NAME_PARAMETER, request );
      Airline airline = getAirline(airlineName);


      XmlDumper dumper = new XmlDumper(response.getWriter());
      dumper.dump(airline);
  }

  /**
   * Handles an HTTP POST request by storing the dictionary entry for the
   * "word" and "definition" request parameters.  It writes the dictionary
   * entry to the HTTP response.
   */
  @Override
  protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
  {
      response.setContentType( "text/plain" );

      String airlineName = getParameter(AIRLINE_NAME_PARAMETER, request );
      if (airlineName == null) {
          missingRequiredParameter(response, AIRLINE_NAME_PARAMETER);
          return;
      }

      Airline airline = getOrCreateAirline(airlineName);

      String flightNumber = getParameter(FLIGHT_NUMBER_PARAMETER, request );
      if ( flightNumber == null) {
          missingRequiredParameter( response, FLIGHT_NUMBER_PARAMETER);
          return;
      }

      String src = getParameter(SOURCE_PARAMETER, request );
      if ( src == null) {
          missingRequiredParameter( response, SOURCE_PARAMETER);
          return;
      }

      String depart = getParameter(DEPARTURE_PARAMETER, request );
      if ( depart == null) {
          missingRequiredParameter( response, DEPARTURE_PARAMETER);
          return;
      }

      String dest = getParameter(DESTINATION_PARAMETER, request );
      if ( dest == null) {
          missingRequiredParameter( response, DESTINATION_PARAMETER);
          return;
      }

      String arrive = getParameter(ARRIVAL_PARAMETER, request );
      if ( arrive == null) {
          missingRequiredParameter( response, ARRIVAL_PARAMETER);
          return;
      }

      try {
          Flight flight = new Flight(Integer.parseInt(flightNumber), src, depart, dest, arrive);
          airline.addFlight(flight);

      } catch (IllegalArgumentException | ParseException ex){
          throw new IllegalArgumentException();
      }

      PrintWriter pw = response.getWriter();
      pw.println(airlineName + flightNumber + src + depart + dest + arrive);
      pw.flush();

      response.setStatus( HttpServletResponse.SC_OK);
  }

    private Airline getOrCreateAirline(String airlineName) {
        Airline airline = getAirline(airlineName);
        if (airline == null) {
            airline = new Airline(airlineName);
            this.airlines.put(airlineName, airline);
        }

        return airline;
    }

    /**
   * Handles an HTTP DELETE request by removing all dictionary entries.  This
   * behavior is exposed for testing purposes only.  It's probably not
   * something that you'd want a real application to expose.
   */
  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      response.setContentType("text/plain");

      this.airlines.clear();

      PrintWriter pw = response.getWriter();
      pw.println(Messages.allDictionaryEntriesDeleted());
      pw.flush();

      response.setStatus(HttpServletResponse.SC_OK);

  }

  /**
   * Writes an error message about a missing parameter to the HTTP response.
   *
   * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
   */
  private void missingRequiredParameter( HttpServletResponse response, String parameterName )
      throws IOException
  {
      String message = Messages.missingRequiredParameter(parameterName);
      response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, message);
  }

  /**
   * Returns the value of the HTTP request parameter with the given name.
   *
   * @return <code>null</code> if the value of the parameter is
   *         <code>null</code> or is the empty string
   */
  private String getParameter(String name, HttpServletRequest request) {
    String value = request.getParameter(name);
    if (value == null || "".equals(value)) {
      return null;

    } else {
      return value;
    }
  }

  @VisibleForTesting
  Airline getAirline(String airlineName) {
      return this.airlines.get(airlineName);
  }
}
