package mongolay.utils;

import models.exceptions.RequestException;
import play.mvc.Http;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class HibernateValidator {

	public static <T> Set<ConstraintViolation<T>> apply(T t) {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		return validator.validate(t);
	}

	public static <T> T validate(T t) throws RequestException {
		Set<ConstraintViolation<T>> errors = HibernateValidator.apply(t);
		if (errors.size() != 0) {
			throw new RequestException(Http.Status.BAD_REQUEST, HibernateValidator.formatErrors(errors));
		}
		return t;
	}

	public static <T> boolean isValid(T t) {
		return HibernateValidator.apply(t).size() == 0;
	}

	public static <T> CompletableFuture<T> validateAsynch(T t, Executor context) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				HibernateValidator.validate(t);
			} catch (RequestException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
			return t;
		}, context);
	}

	public static <T> List<String> formatErrors (Set<ConstraintViolation<T>> errors) {
		return errors.stream()
				.map(err -> String.format("%s %s", err.getPropertyPath(), err.getMessage()))
				.collect(Collectors.toList());
	}
}
