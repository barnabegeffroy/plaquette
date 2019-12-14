package io.github.oliviercailloux.plaquette_mido_soap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import schemas.ebx.dataservices_1.MentionType.Root.Mention;
import schemas.ebx.dataservices_1.ProgramType.Root.Program;

class QueryTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(QueryTests.class);
	private final Querier querier;

	@BeforeAll
	static void setDefaultAuthenticator() throws Exception {
		QueriesHelper.setDefaultAuthenticator();
	}

	public QueryTests() {
		querier = new Querier();
	}

	@Test
	void testRelativePredicate() throws Exception {
		final String predicate = "../../root/Mention/mentionID = '" + M1ApprBuilder.MENTION_ID + "'";
		final List<Mention> mentions = querier.getMentions(predicate);
		assertEquals(1, mentions.size());
		assertEquals(M1ApprBuilder.MENTION_ID, Iterables.getOnlyElement(mentions).getMentionID());
	}

	@Test
	void testForeignKeyPredicateAndProgram() throws Exception {
		/**
		 * NB references next to refMention are limited to primary keys, see Extraction
		 * de clés étrangères.
		 */
		final String predicate = "ident = '" + M1ApprBuilder.PROGRAM_IDENT + "' and refMention = '"
				+ M1ApprBuilder.MENTION_ID + "' and refMention/mentionID = '" + M1ApprBuilder.MENTION_ID + "'";

		final List<Program> programs = querier.getPrograms(predicate);
		assertEquals(1, programs.size());
		final Program program = Iterables.getOnlyElement(programs);
		assertEquals(M1ApprBuilder.PROGRAM_IDENT, program.getIdent().getValue());
		assertEquals(M1ApprBuilder.PROGRAM_NAME, program.getProgramName().getValue());
		final List<String> subPrograms = program.getProgramStructure().getValue().getRefProgram();
		LOGGER.info("Sub program: {}.", subPrograms);
		assertEquals(2, subPrograms.size());
		assertTrue(subPrograms.get(0).endsWith("-100-S1"));
		assertTrue(subPrograms.get(1).endsWith("-100-S2"));
	}

	@Test
	void testGetCoursesSemester1() throws Exception {
		final String programId = M1ApprBuilder.PROGRAM_ID_S1_L1;
		final Program program = querier.getProgram(programId);
		assertEquals("UE Obligatoires", program.getProgramName().getValue());
		assertEquals(M1ApprBuilder.MENTION_ID, program.getRefMention().getValue());
		final List<String> subPrograms = program.getProgramStructure().getValue().getRefProgram();
		assertEquals(0, subPrograms.size());
		final List<String> courses = program.getProgramStructure().getValue().getRefCourse();
		assertFalse(courses.isEmpty());
	}

}
