package io.github.oliviercailloux.plaquette_mido_soap;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Verify;
import com.google.common.collect.Iterables;

import ebx.ebx_dataservices.EbxDataservices;
import ebx.ebx_dataservices.EbxDataservicesService;
import ebx.ebx_dataservices.StandardException;
import io.github.oliviercailloux.xml_utils.XmlUtils;
import schemas.ebx.dataservices_1.CourseType.Root.Course;
import schemas.ebx.dataservices_1.MentionType.Root.Mention;
import schemas.ebx.dataservices_1.ObjectFactory;
import schemas.ebx.dataservices_1.ProgramType.Root.Program;
import schemas.ebx.dataservices_1.SelectCourseRequestType;
import schemas.ebx.dataservices_1.SelectCourseResponseType;
import schemas.ebx.dataservices_1.SelectMentionRequestType;
import schemas.ebx.dataservices_1.SelectMentionResponseType;
import schemas.ebx.dataservices_1.SelectProgramRequestType;
import schemas.ebx.dataservices_1.SelectProgramResponseType;

public class Querier {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Querier.class);

	private final EbxDataservices dataservices;

	public Querier() {
		dataservices = new EbxDataservicesService().getEbxDataservices();
	}

	public List<Mention> getMentions(String predicate) throws StandardException {
		final SelectMentionRequestType request = new SelectMentionRequestType();
		request.setBranch("pvRefRof");
		request.setInstance("RefRof");
		request.setPredicate(predicate);
		LOGGER.debug("Request: {}.", XmlUtils.toXml(new ObjectFactory().createSelectMention(request)));
		final SelectMentionResponseType result = dataservices.selectMentionOperation(request);
		LOGGER.debug("Result: {}.", XmlUtils.toXml(new ObjectFactory().createSelectMentionResponse(result)));
		return result.getData().getRoot().getMention();
	}

	public Mention getMention(String mentionId) throws StandardException {
		final String predicate = "mentionID = '" + mentionId + "'";
		final List<Mention> mentions = getMentions(predicate);
		Verify.verify(mentions.size() == 1);
		final Mention mention = Iterables.getOnlyElement(mentions);
		Verify.verify(mention.getMentionID().equals(mentionId));
		return mention;
	}

	public List<Program> getPrograms(String predicate) throws StandardException {
		final SelectProgramRequestType request = new SelectProgramRequestType();
		request.setBranch("pvRefRof");
		request.setInstance("RefRof");
		request.setPredicate(predicate);
		LOGGER.debug("Request: {}.", XmlUtils.toXml(new ObjectFactory().createSelectProgram(request)));
		final SelectProgramResponseType result = dataservices.selectProgramOperation(request);
		LOGGER.debug("Result: {}.", XmlUtils.toXml(new ObjectFactory().createSelectProgramResponse(result)));
		return result.getData().getRoot().getProgram();
	}

	public Program getProgram(String programId) throws StandardException {
		final String predicate = "programID = '" + programId + "'";
		final List<Program> programs = getPrograms(predicate);
		Verify.verify(programs.size() == 1);
		final Program program = Iterables.getOnlyElement(programs);
		Verify.verify(program.getProgramID().equals(programId));
		return program;
	}

	public List<Course> getCourses(String predicate) throws StandardException {
		final SelectCourseRequestType request = new SelectCourseRequestType();
		request.setBranch("pvRefRof");
		request.setInstance("RefRof");
		request.setPredicate(predicate);
		LOGGER.debug("Request: {}.", XmlUtils.toXml(new ObjectFactory().createSelectCourse(request)));
		final SelectCourseResponseType result = dataservices.selectCourseOperation(request);
		LOGGER.debug("Result: {}.", XmlUtils.toXml(new ObjectFactory().createSelectCourseResponse(result)));
		return result.getData().getRoot().getCourse();
	}

	public Course getCourse(String courseId) throws StandardException {
		final String predicate = "courseID = '" + courseId + "'";
		final List<Course> courses = getCourses(predicate);
		Verify.verify(courses.size() == 1);
		final Course course = Iterables.getOnlyElement(courses);
		Verify.verify(course.getCourseID().equals(courseId));
		return course;
	}
}
